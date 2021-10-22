package gleb.apps.pyhie.mainHabits.sleepingHabit.planningHabit

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import gleb.apps.pyhie.BR
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.databinding.FragmentPlanningHabitMainBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.EatingInfo
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingInfo
import gleb.apps.pyhie.recyclerview.RecyclerItem
import gleb.apps.pyhie.recyclerview.RecyclerViewAdapter
import gleb.apps.pyhie.util.GetDates
import gleb.apps.pyhie.util.ViewAnimation
import kotlinx.coroutines.launch
import java.util.*


class PlanningHabitTomorrow : Fragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentPlanningHabitMainBinding
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var planningViewModel: PlanningHabitViewModel
    private lateinit var viewModelFactory: PlanningViewModelFactory
    private lateinit var plannerList: List<RecyclerItem>
    private lateinit var plannerListData: List<PlannerData>
    private lateinit var submitTime: Button
    private lateinit var eatingInfo: EatingInfo
    private lateinit var sleepingInfo: SleepingInfo
    private lateinit var addMoreDialog: Dialog
    private lateinit var recyclerItemList: MutableList<RecyclerItem>
    private lateinit var sharedPref: SharedPref
    private var email = ""
    private var rotate = false
    private var hour = 0
    private var minute = 0
    private var getHour = 0
    private var getMinute = 0
    private var numberOfActivities = 0
    private lateinit var dialogSubmit: Button

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
                val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
                builder.setTitle("Delete this activity")
                builder.setMessage("Are you sure?")
                builder.setPositiveButton(R.string.yes) { dialog, which ->
                    planningViewModel.deletePlannerDataTomorrow(plannerData)
                    updateRecViewData()
                    dialog.dismiss()
                }
                builder.setNegativeButton(R.string.no) { dialog, which -> dialog.dismiss() }
                builder.show()

            })

        binding = FragmentPlanningHabitMainBinding.inflate(inflater, container, false).apply {
            recyclerView.adapter = recyclerViewAdapter
            todayButton.isEnabled = true
            tomorrowButton.isEnabled = false
            submit.setOnClickListener { submit() }
            todayButton.setOnClickListener { findNavController().popBackStack() }
            @RequiresApi(Build.VERSION_CODES.O)
            date.text = GetDates().tomorrowDate()


        }

        binding.lifecycleOwner = this
        addMoreDialog = Dialog(requireContext())

        planningViewModel.eatingData.observe(viewLifecycleOwner, Observer { eatingInfo = it })
        planningViewModel.sleepingInfo.observe(viewLifecycleOwner, Observer { sleepingInfo = it })
        planningViewModel.plannerDataTomorrow.observe(
            viewLifecycleOwner,
            Observer { plannerList = it })

        updateRecViewData()
        floatingButtons()
        swipeToDelete()

        return binding.root
    }

    private fun updateRecViewData() {
        lifecycleScope.launch {
            recyclerItemList = FirebaseService.getPlannerDataTomorrowList(email)?.map {
                it.toRecyclerItem()
            } as MutableList<RecyclerItem>
            recyclerViewAdapter.updateData(recyclerItemList)
            binding.progressBar.visibility = View.GONE
            numberOfActivities = recyclerItemList.size
            val activitiesRequired = 8 - numberOfActivities
            val completeString = if (numberOfActivities >= 8) getString(
                R.string.complete_planner,
                ""
            ) else getString(
                R.string.complete_planner,
                "$activitiesRequired more activities required"
            )
            binding.subTittle.text =
                if (SharedPref(requireActivity()).getBoolean(Keys.PLANNER_TOM_NOT_SUBMITTED)) completeString else getString(
                    R.string.tomorrow_is_completed
                )
        }
    }

    private fun swipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT) ){
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
                    recyclerItemList.size -> {
                        Toast.makeText(requireContext(), "Cannot remove sleep from today's planner", Toast.LENGTH_SHORT).show()
                        updateRecViewData()
                    }
                    else -> {
                        lifecycleScope.launch {
                            plannerListData = FirebaseService.getPlannerDataTomorrowList(email)!!
                            val removedItem = plannerListData[viewHolder.adapterPosition]
                            FirebaseService.deletePlannerDataTomorrow(email,removedItem)
                            updateRecViewData()
                        }
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
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


                } else {
                    ViewAnimation().showOut(addMore)
                    ViewAnimation().showOut(submit)

                }
            }

            addMore.setOnClickListener { addMoreDialog() }
        }
    }

    private fun addMoreDialog() {
        addMoreDialog.setContentView(R.layout.dialog_add_more_planner)
        val title = addMoreDialog.findViewById<EditText>(R.id.title)
        val description = addMoreDialog.findViewById<EditText>(R.id.description)
        dialogSubmit =
            addMoreDialog.findViewById<ExtendedFloatingActionButton>(R.id.submit_button)
        dialogSubmit.isEnabled = false
        submitTime = addMoreDialog.findViewById<Button>(R.id.select_time)
        addMoreDialog.show()

        submitTime.setOnClickListener {
            openTimePicker()
        }

        dialogSubmit.setOnClickListener {
            val newPlannerData = PlannerData(
                getHour,
                getMinute,
                plannerList.size.toString(),
                title.text.toString(),
                description.text.toString()
            )

            planningViewModel.insertPlannerDataTomorrow(newPlannerData)
            updateRecViewData()
            addMoreDialog.dismiss()
        }

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        getHour = hourOfDay
        getMinute = minute
        val time = getHour.toDouble() + (getMinute.toDouble() / 60)

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

    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
        TimePickerDialog(
            this.context, this
            , hour, minute, true
        ).show()
    }

    private fun submit() {
        if (numberOfActivities >= 8) {
            SharedPref(requireActivity()).saveBoolean(Keys.PLANNER_TOM_NOT_SUBMITTED, false)
            binding.subTittle.text = getString(R.string.tomorrow_is_completed)
            Toast.makeText(
                requireContext(),
                "Thank you! Tomorrows planner is submitted.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(requireContext(), "You need to add more activities, to complete tomorrows planner.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun PlannerData.toRecyclerItem() =
        RecyclerItem(this, R.layout.planner_item, BR.plannerData)


}