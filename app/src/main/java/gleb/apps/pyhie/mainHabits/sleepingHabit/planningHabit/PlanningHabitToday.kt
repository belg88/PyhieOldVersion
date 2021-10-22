
package gleb.apps.pyhie.mainHabits.sleepingHabit.planningHabit

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import gleb.apps.pyhie.*
import gleb.apps.pyhie.databinding.FragmentPlanningHabitMainBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.mainHabits.sleepingHabit.PenaltyFragment
import gleb.apps.pyhie.pojos.EatingInfo
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingInfo
import gleb.apps.pyhie.recyclerview.RecyclerItem
import gleb.apps.pyhie.recyclerview.RecyclerViewAdapter
import gleb.apps.pyhie.util.*
import kotlinx.coroutines.launch
import java.util.*

class PlanningHabitToday : Fragment(), TimePickerDialog.OnTimeSetListener {
    private var email = ""
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModelFactory: PlanningViewModelFactory
    private lateinit var planningViewModel: PlanningHabitViewModel
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var binding: FragmentPlanningHabitMainBinding
    private lateinit var addMoreDialog: Dialog
    private lateinit var eatingInfo: EatingInfo
    private lateinit var sleepingInfo: SleepingInfo
    private lateinit var plannerItemsList: List<RecyclerItem>
    private lateinit var plannerList: MutableList<PlannerData>
    private var rotate = false
    private var hour = 0
    private var minute = 0
    private var getHour = 0
    private var getMinute = 0
    private lateinit var dialogSubmit: Button
    private lateinit var submitTime: Button
    private var plannerInfo = PlannerInfo()
    var remainingActivities = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = SharedPref(requireActivity()).getString(Keys.EMAIL)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPref = SharedPref(requireActivity())
        viewModelFactory = PlanningViewModelFactory(email)
        planningViewModel =
            ViewModelProvider(this, viewModelFactory)[PlanningHabitViewModel::class.java]


        recyclerViewAdapter =
            RecyclerViewAdapter(RecyclerViewAdapter.OnItemClickListener { recyclerItem, position ->
                val plannerData = recyclerItem.data as PlannerData
                val alertBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
                alertBuilder.setTitle("Mark this activity as completed?")
                alertBuilder.setPositiveButton(R.string.yes) { dialog, which ->
                    val newPlannerData = PlannerData(
                        plannerData.startTimeHour,
                        plannerData.startTimeMinute,
                        plannerData.identifier,
                        plannerData.title,
                        plannerData.description,
                        plannerData.firstActivity,
                        plannerData.lastActivity,
                        plannerData.bedTimePastMidnight,
                        true
                    )
                    planningViewModel.insertPlannerDataToday(newPlannerData)
                    updateRecViewData()
                    remainingActivities -= 1
                    binding.subTittle.text =
                        getString(R.string.remaining_activities, remainingActivities)
                    dialog.dismiss()
                }

                alertBuilder.setNegativeButton(R.string.no) { dialog, which ->
                    dialog.dismiss()

                }
                alertBuilder.show()
            })

        binding = FragmentPlanningHabitMainBinding.inflate(inflater, container, false).apply {
            recyclerView.adapter = recyclerViewAdapter
            date.text = GetDates().todayDate
            todayButton.isEnabled = false
            tomorrowButton.isEnabled = true
            tomorrowButton.setOnClickListener {
                findNavController().navigate(
                    PlanningHabitTodayDirections.actionPlanningHabitTodayToPlanningHabitMain()
                )
            }
            howItWorks.setOnClickListener { findNavController().navigate(PlanningHabitTodayDirections.actionPlanningHabitTodayToHowItWorksFragment("PlanningHabit")) }

        }
        binding.lifecycleOwner = this
        addMoreDialog = Dialog(requireContext())

        floatingButtons()
        swipeToDelete()

        return binding.root
    }

    private fun swipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT) ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (viewHolder.adapterPosition) {
                    0 -> {
                        Toast.makeText(requireContext(), "Cannot remove sleep from today's planner", Toast.LENGTH_SHORT).show()
                        updateRecViewData()
                    }
                    plannerList.size -> {
                        Toast.makeText(requireContext(), "Cannot remove sleep from today's planner", Toast.LENGTH_SHORT).show()
                        updateRecViewData()
                    }
                    else -> {
                        lifecycleScope.launch {
                            val removedItem = plannerList[viewHolder.adapterPosition]
                            FirebaseService.deletePlannerDataToday(email,removedItem)
                            updateRecViewData()
                        }
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            lifecycleScope.launchWhenResumed {
                plannerInfo = FirebaseService.getPlannerInfo(email)!!
                submit.isEnabled = !plannerInfo.submitted
                remainingActivities = remainingActivities()
                subTittle.text = if (plannerInfo.submitted) getString(
                    R.string.main_habit_is_completed,
                    "Planner data"
                ) else getString(R.string.remaining_activities, remainingActivities)
                resetHabit()
                checkBedEatTimes()
            }.invokeOnCompletion {
                updateRecViewData()
                 }
        }

    }

    private fun PlannerData.toRecyclerItem() =
        RecyclerItem(this, R.layout.planner_item, BR.plannerData)

    private fun updateRecViewData() {
        lifecycleScope.launch {
            plannerItemsList = FirebaseService.getPlannerDataTodayList(email)?.map {
                it.toRecyclerItem()
            }!!
            recyclerViewAdapter.updateData(plannerItemsList)
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun floatingButtons() {
        binding.apply {
            ViewAnimation().init(addMore)
            ViewAnimation().init(submit)
            ViewAnimation().init(howItWorks)
            binding.fab.setOnClickListener {
                rotate = ViewAnimation().rotateFab(it, !rotate)

                if (rotate) {
                    ViewAnimation().showIn(addMore)
                    ViewAnimation().showIn(submit)
                    ViewAnimation().showIn(howItWorks)

                } else {
                    ViewAnimation().showOut(addMore)
                    ViewAnimation().showOut(submit)
                    ViewAnimation().showOut(howItWorks)
                }
            }

            addMore.setOnClickListener { addMoreDialog() }
            submit.setOnClickListener { submitHabit() }
        }
    }

    private fun submitHabit() {
        var points = 0.0
        if (sharedPref.getBoolean(Keys.PLANNER_TOM_NOT_SUBMITTED)) {
            Toast.makeText(
                requireContext(),
                "You must complete Tomorrows planner, before submitting Today's planner ",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (remainingActivities > 0) {
                val builder =
                    AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
                builder.setTitle("You still have $remainingActivities uncompleted activities!")
                builder.setMessage("Do you want to submit this habit?")
                builder.setPositiveButton(R.string.yes) { _, _ ->
                    when (remainingActivities) {
                        1 -> points = UserConstants.MEDIUM_POINTS
                        2 -> points = UserConstants.SMALL_POINTS
                        3 -> points = UserConstants.MIN_POINTS
                        else -> points = 0.0
                    }

                    sharedPref.saveBoolean(Keys.PLANNER_NOT_SUBMITTED, false)
                    findNavController().navigate(
                        PlanningHabitTodayDirections.actionPlanningHabitTodayToHabitIsCompleted(
                            getString(R.string.daily_planing),
                            points.toFloat()
                        )
                    )
                    submitPlannerInfo(points)
                    activateFirstChallenge()
                }
                builder.setNegativeButton(R.string.no) { dialog, which -> dialog.dismiss() }
                builder.show()
            } else {
                points = UserConstants.MAX_POINTS
                findNavController().navigate(
                    PlanningHabitTodayDirections.actionPlanningHabitTodayToHabitIsCompleted(
                        getString(R.string.daily_planing),
                        points.toFloat()
                    )
                )
                submitPlannerInfo(points)
                sharedPref.saveBoolean(Keys.PLANNER_NOT_SUBMITTED, false)
                activateFirstChallenge()
            }
        }
    }

    private fun submitPlannerInfo(points: Double) {
        lifecycleScope.launch {
            plannerInfo.currentDay = GetDates().today
            plannerInfo.currentYear = GetDates().year
            plannerInfo.numberOfSubmits = plannerInfo.numberOfSubmits + 1
            plannerInfo.totalPoints = plannerInfo.totalPoints + points
            plannerInfo.submitted = true
            val plannerBalance = (plannerInfo.totalPoints / plannerInfo.numberOfSubmits) * 10
            sharedPref.saveInt(Keys.PLANNER_BALANCE, plannerBalance.toInt())
            FirebaseService.insertPlannerInfo(plannerInfo, email)
        }
    }

    private fun addMoreDialog() {
        addMoreDialog.setContentView(R.layout.dialog_add_more_planner)
        val title = addMoreDialog.findViewById<EditText>(R.id.title)
        val description = addMoreDialog.findViewById<EditText>(R.id.description)
        dialogSubmit =
            addMoreDialog.findViewById<ExtendedFloatingActionButton>(R.id.submit_button)
        dialogSubmit.isEnabled = false
        submitTime = addMoreDialog.findViewById(R.id.select_time)
        addMoreDialog.show()

        submitTime.setOnClickListener {
            openTimePicker()
        }

        dialogSubmit.setOnClickListener {
            val newPlannerData = PlannerData(
                getHour,
                getMinute,
                plannerItemsList.size.toString(),
                title.text.toString(),
                description.text.toString()
            )
            remainingActivities += 1
            binding.subTittle.text = getString(R.string.remaining_activities, remainingActivities)
            planningViewModel.insertPlannerDataToday(newPlannerData)
            updateRecViewData()
            addMoreDialog.dismiss()
        }
    }

    private suspend fun resetHabit() {
        val reset = ResetHabit(plannerInfo.currentDay, plannerInfo.currentYear)
            if (reset.isNextDay()) {
                plannerInfo.currentDay = GetDates().today
                plannerInfo.currentYear = GetDates().year
                sharedPref.saveBoolean(Keys.PLANNER_TOM_NOT_SUBMITTED, true)
                val todayList = FirebaseService.getPlannerDataTodayList(email)
                if (todayList != null) {
                    for (x in todayList.indices) {
                        planningViewModel.deletePlannerDataToday(todayList[x])
                    }
                }
                val tomorrowList = FirebaseService.getPlannerDataTomorrowList(email)
                if (tomorrowList != null) {
                    for (x in tomorrowList.indices) {
                        planningViewModel.insertPlannerDataToday(tomorrowList[x])
                    }
                    binding.subTittle.text =
                        getString(R.string.remaining_activities, tomorrowList.size)
                }
                if (!plannerInfo.submitted) {
                    val penaltyDialog = PenaltyFragment()
                    penaltyDialog.show(parentFragmentManager, PenaltyFragment.TAG)

                } else {
                    plannerInfo.submitted = false
                }
                FirebaseService.insertPlannerInfo(plannerInfo, email)
        }
    }

    private suspend fun checkBedEatTimes() {

        if (!sharedPref.getBoolean(Keys.SLEEP1_NOT_SUBMITTED)) {
            markBedTimeCompleted(0, "", false, true)
            markWokeTimeCompleted(0, "", true, false)
            remainingActivities -= 1
        }
        if (!sharedPref.getBoolean(Keys.SLEEP2_NOT_SUBMITTED)) {
            markBedTimeCompleted(1, "for second sleep", false, false)
            markWokeTimeCompleted(1, "for second sleep", false, false)
            remainingActivities -= 1
        }
        if (!sharedPref.getBoolean(Keys.SLEEP3_NOT_SUBMITTED)) {
            markBedTimeCompleted(2, "for third sleep", false, false)
            markWokeTimeCompleted(2, "for third sleep", false, false)
            remainingActivities -= 1
        }
        if (!sharedPref.getBoolean(Keys.EAT1_NOT_SUBMITTED)) {
            markMealCompletedInPlanner(0, "First")
            remainingActivities -= 1
        }
        if (!sharedPref.getBoolean(Keys.EAT2_NOT_SUBMITTED)) {
            remainingActivities -= 1
            markMealCompletedInPlanner(1, "Second")
        }
        if (!sharedPref.getBoolean(Keys.EAT3_NOT_SUBMITTED)) {
            remainingActivities -= 1
            markMealCompletedInPlanner(2, "Third")
        }
        if (!sharedPref.getBoolean(Keys.EAT4_NOT_SUBMITTED)) {
            remainingActivities -= 1
            markMealCompletedInPlanner(3, "Fourth")
        }
        if (!sharedPref.getBoolean(Keys.EAT5_NOT_SUBMITTED)) {
            remainingActivities -= 1
            markMealCompletedInPlanner(4, "Fifth")
        }
    }

    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
        TimePickerDialog(
            this.context, this
            , hour, minute, true
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        getHour = hourOfDay
        getMinute = minute
        val time = getHour.toDouble() + (getMinute.toDouble() / 60)

        lifecycleScope.launch {
            sleepingInfo = FirebaseService.getSleepingPref(email)!!
            val wokeUpTime =
                sleepingInfo.prefWokeUpHours[0].toDouble() + (sleepingInfo.prefWokeUpMinutes[0].toDouble() / 60)
            val bedTime =
                sleepingInfo.prefBedTimeHours[0].toDouble() + (sleepingInfo.prefBedTimeMinutes[0].toDouble() / 60)

            val bedTimePastMidnight =
                sleepingInfo.prefBedTimeHours[0] < sleepingInfo.prefWokeUpHours[0]

            if (bedTimePastMidnight) {
                if (time < wokeUpTime && time > bedTime) {
                    Toast.makeText(
                        requireContext(),
                        "You can't have an activity while you sleeping.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    dialogSubmit.isEnabled = true
                    submitTime.text = getString(R.string.time, getHour, getMinute)

                }
            } else {
                if (time < wokeUpTime || time > bedTime) {
                    Toast.makeText(
                        requireContext(),
                        "You can't have an activity while you sleeping.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    dialogSubmit.isEnabled = true
                    submitTime.text = getString(R.string.time, getHour, getMinute)
                }
            }
        }
    }

    private suspend fun remainingActivities(): Int {
        var numberOfSubmitted = 0
        var numberNeedToSubmit = 0
        plannerList = mutableListOf(PlannerData())
        plannerList = FirebaseService.getPlannerDataTodayList(email)!! as MutableList<PlannerData>

        for (x in plannerList.indices) {
            if (plannerList[x].marked) numberOfSubmitted += 1

            numberNeedToSubmit = plannerList.size - numberOfSubmitted

        }
        return numberNeedToSubmit
    }

    private suspend fun markBedTimeCompleted(
        index: Int,
        sleepDescription: String,
        firstActivity: Boolean,
        lastActivity: Boolean
    ) {
        sleepingInfo = FirebaseService.getSleepingPref(email)!!
        val bedTimePastMidnight =
            sleepingInfo.prefBedTimeHours[0] < sleepingInfo.prefWokeUpHours[0]

        FirebaseService.insertPlannerDataToday(
            PlannerData(
                sleepingInfo.prefBedTimeHours[index],
                sleepingInfo.prefBedTimeMinutes[index],
                "",
                getString(R.string.bed_time_planner, sleepDescription),
                "Sweet dreams...",
                firstActivity,
                lastActivity,
                bedTimePastMidnight,
                true
            ), email
        )
    }

    private suspend fun markWokeTimeCompleted(
        index: Int,
        sleepDescription: String,
        firstActivity: Boolean,
        lastActivity: Boolean
    ) {
        sleepingInfo = FirebaseService.getSleepingPref(email)!!
        FirebaseService.insertPlannerDataToday(
            PlannerData(
                sleepingInfo.prefWokeUpHours[index],
                sleepingInfo.prefWokeUpMinutes[index],
                "",
                getString(R.string.woke_up_time_planner, sleepDescription),
                "Rise and shine!",
                firstActivity, lastActivity, false,
                true
            ), email
        )
    }

    private suspend fun markMealCompletedInPlanner(index: Int, mealDescription: String) {
        eatingInfo = FirebaseService.getEatingInfo(email)!!
        FirebaseService.insertPlannerDataToday(
            PlannerData(
                eatingInfo.mealHour[index],
                eatingInfo.mealMinute[index],
                "",
                getString(R.string.meals_planner, mealDescription),
                "Bon appetit..",
                false,
                false,
                false,
                true
            ), email
        )

    }

    private fun activateFirstChallenge() {
        if (sharedPref.getInt(Keys.CURRENT_CHALLENGE) == 0) sharedPref.saveInt(
            Keys.CURRENT_CHALLENGE,
            1
        )
    }
}