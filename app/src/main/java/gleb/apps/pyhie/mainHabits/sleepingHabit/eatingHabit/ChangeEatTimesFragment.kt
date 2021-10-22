package gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.databinding.FragmentChangeEatTimesBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.EatingInfo
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingInfo
import kotlinx.coroutines.launch
import java.util.*


class ChangeEatTimesFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentChangeEatTimesBinding
    private lateinit var viewModel: EatingViewModel
    private lateinit var viewModelFactory: EatingViewModelFactory
    private val args: ChangeEatTimesFragmentArgs by navArgs()
    private lateinit var eatingInfo: EatingInfo
    private var tag = 0
    private var hour = 0
    private var minute = 0
    private var prefMealHourList = mutableListOf(0, 0, 0, 0, 0)
    private var prefMealMinuteList = mutableListOf(0, 0, 0, 0, 0)
    private var numberOfMeals = 1
    private var eatingSavedState = EatingSavedState()
    private var sleepingInfo: SleepingInfo = SleepingInfo()
    var email = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eatingInfo = args.eatingInfo
        numberOfMeals = eatingInfo.numberOfMeals
        prefMealMinuteList = eatingInfo.mealMinute
        prefMealHourList = eatingInfo.mealHour
        val gson = Gson()
        val json = SharedPref(requireActivity()).getString(Keys.EAT_SAVED_STATE)
        val type = object : TypeToken<EatingSavedState?>() {}.type
        eatingSavedState = gson.fromJson<EatingSavedState>(json, type)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        email = SharedPref(requireActivity()).getString(Keys.EMAIL)!!
        binding = FragmentChangeEatTimesBinding.inflate(inflater, container, false)
        viewModelFactory = EatingViewModelFactory(email)
        viewModel = ViewModelProvider(this, viewModelFactory)[EatingViewModel::class.java]

        lifecycleScope.launch {
            sleepingInfo = FirebaseService.getSleepingPref(email)!!
        }

        binding.apply {
            toolbar.navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.arrow_back, resources.newTheme())
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            changeMeal1.setOnClickListener {
                tag = 0
                openTimePicker()
            }
            changeMeal2.setOnClickListener {
                tag = 1
                openTimePicker()
            }
            changeMeal3.setOnClickListener {
                tag = 2
                openTimePicker()
            }
            changeMeal4.setOnClickListener {
                tag = 3
                openTimePicker()
            }
            changeMeal5.setOnClickListener {
                tag = 4
                openTimePicker()
            }

            removeMeal2.setOnClickListener {
                layout2.isVisible = false
                prefMealHourList[1] = 0
                prefMealMinuteList[1] = 0
                snackBar(1, layout2, mealText2, R.string.pref_meal2)
                numberOfMeals -= 1
            }

            removeMeal3.setOnClickListener {
                layout3.isVisible = false
                prefMealHourList[2] = 0
                prefMealMinuteList[2] = 0
                numberOfMeals -= 1
                snackBar(2, layout3, mealText3, R.string.pref_meal3)
            }

            removeMeal4.setOnClickListener {
                layout4.isVisible = false
                prefMealHourList[3] = 0
                prefMealMinuteList[3] = 0
                numberOfMeals -= 1
                snackBar(3, layout4, mealText4, R.string.pref_meal4)
            }

            removeMeal5.setOnClickListener {
                layout5.isVisible = false
                prefMealHourList[4] = 0
                prefMealMinuteList[4] = 0
                numberOfMeals -= 1
                snackBar(4, layout5, mealText5, R.string.pref_meal5)
            }

            addMore.setOnClickListener {
                when (numberOfMeals) {
                    1 -> {
                        numberOfMeals += 1
                        layout2.isVisible = true
                    }
                    2 -> {
                        numberOfMeals += 1
                        layout3.isVisible = true
                    }
                    3 -> {
                        numberOfMeals += 1
                        layout4.isVisible = true
                    }
                    4 -> {
                        numberOfMeals += 1
                        layout5.isVisible = true
                    }
                    5 -> {
                        Toast.makeText(
                            requireContext(),
                            "Maximum number of meals has been reached",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            submitButton.setOnClickListener {checkTimesBeforeSubmit()}
        }
        setViews()

        return binding.root
    }

    private fun submitNewTimes() {
        val newEatInfo = EatingInfo(
            prefMealHourList,
            prefMealMinuteList,
            numberOfMeals,
            eatingInfo.submitted,
            eatingInfo.currentDay,
            eatingInfo.currentYear,
            eatingInfo.numberOfSubmits
        )
        viewModel.insertEatingInfo(newEatInfo)

        if (newEatInfo.numberOfMeals > 0) {
            val plannerData = PlannerData(
                prefMealHourList[0],
                prefMealMinuteList[0],
                "",
                getString(R.string.meals_planner, "First"),
                "Bon appetit.."
            )
            viewModel.insertPlannerDataToday(plannerData)
            viewModel.insertPlannerDataTomorrow(plannerData)
        }

        if (newEatInfo.numberOfMeals > 1) {
            val plannerData = PlannerData(
                prefMealHourList[1],
                prefMealMinuteList[1],
                "",
                getString(R.string.meals_planner, "Second"),
                "Bon appetit.."
            )
            viewModel.insertPlannerDataToday(plannerData)
            viewModel.insertPlannerDataTomorrow(plannerData)
        }

        if (newEatInfo.numberOfMeals > 2) {
            val plannerData = PlannerData(
                prefMealHourList[2],
                prefMealMinuteList[2],
                "",
                getString(R.string.meals_planner, "Third"),
                "Bon appetit.."
            )
            viewModel.insertPlannerDataToday(plannerData)
            viewModel.insertPlannerDataTomorrow(plannerData)
        }

        if (newEatInfo.numberOfMeals > 3) {
            val plannerData = PlannerData(
                prefMealHourList[3],
                prefMealMinuteList[3],
                "",
                getString(R.string.meals_planner, "Fourth"),
                "Bon appetit.."
            )
            viewModel.insertPlannerDataToday(plannerData)
            viewModel.insertPlannerDataTomorrow(plannerData)
        }

        if (newEatInfo.numberOfMeals > 4) {
            val plannerData = PlannerData(
                prefMealHourList[4],
                prefMealMinuteList[4],
                "",
                getString(R.string.meals_planner, "Fifth"),
                "Bon appetit.."
            )
            viewModel.insertPlannerDataToday(plannerData)
            viewModel.insertPlannerDataTomorrow(plannerData)
        }
        when (numberOfMeals) {
            1 ->{
                deletePlannedData(1,"Second" )
                deletePlannedData(2,"Third" )
                deletePlannedData(3,"Fourth" )
                deletePlannedData(4,"Fifth" )
            }
            2 -> {
                deletePlannedData(2,"Third" )
                deletePlannedData(3,"Fourth" )
                deletePlannedData(4,"Fifth" )
            }
            3 -> {
                deletePlannedData(3,"Fourth" )
                deletePlannedData(4,"Fifth" )
            }
            4 -> {
                deletePlannedData(4,"Fifth" )
            }
        }
        findNavController().navigate(ChangeEatTimesFragmentDirections.actionChangeEatTimesFragmentToEatingMain(newEatInfo))

        if (!eatingInfo.submitted) {
            eatingSavedState = EatingSavedState(newEatInfo)
            val gson = Gson()
            val gsonString = gson.toJson(
                eatingSavedState
            )
            SharedPref(requireActivity()).saveString(Keys.EAT_SAVED_STATE, gsonString)
        }

    }

    private fun checkTimesBeforeSubmit() {
        var submitTimes = true
        val meal1 = prefMealHourList[0].toDouble() + (prefMealMinuteList[0].toDouble()/60)
        val meal2 = prefMealHourList[1].toDouble() + (prefMealMinuteList[1].toDouble()/60)
        val meal3 = prefMealHourList[2].toDouble() + (prefMealMinuteList[2].toDouble()/60)
        val meal4 = prefMealHourList[3].toDouble() + (prefMealMinuteList[3].toDouble()/60)
        val meal5 = prefMealHourList[4].toDouble() + (prefMealMinuteList[4].toDouble()/60)
        val wokeUp = sleepingInfo.prefWokeUpHours[0].toDouble() + (sleepingInfo.prefWokeUpMinutes[0].toDouble()/60)
        val bedTime = sleepingInfo.prefBedTimeHours[0].toDouble() + (sleepingInfo.prefBedTimeMinutes[0].toDouble()/60)

        when (wokeUp<bedTime) {
            true -> {
                if (meal1<wokeUp || meal1>bedTime) {
                    Toast.makeText(requireContext(), "First meal time must be between main sleeps,and before midnight.", Toast.LENGTH_LONG).show()
                    submitTimes = false
                    }
            }
            false -> if (meal1<wokeUp) {
                Toast.makeText(requireContext(), "First meal time must be between main sleeps,and before midnight.", Toast.LENGTH_LONG).show()
                submitTimes = false
            }
        }
        if(numberOfMeals>1) {
            if (meal2 < meal1) {
                Toast.makeText(
                    requireContext(),
                    "Second meal must be after first meal,and before midnight.",
                    Toast.LENGTH_LONG
                ).show()
                submitTimes = false
            }
        }
        if(numberOfMeals>2) {
            if (meal3 < meal2) {
                Toast.makeText(
                    requireContext(),
                    "Third meal must be after second meal,and before midnight.",
                    Toast.LENGTH_LONG
                ).show()
                submitTimes = false
            }
        }
        if(numberOfMeals>3) {
            if (meal4 < meal3) {
                Toast.makeText(
                    requireContext(),
                    "Fourth meal must be after third meal,and before midnight.",
                    Toast.LENGTH_LONG
                ).show()
                submitTimes = false
            }
        }
        if(numberOfMeals>4) {
            if (meal5 < meal4) {
                Toast.makeText(
                    requireContext(),
                    "Fifth meal must be after fourth meal,and before midnight.",
                    Toast.LENGTH_LONG
                ).show()
                submitTimes = false
            }
        }

        if (submitTimes) submitNewTimes()
    }

    private fun deletePlannedData(index: Int, identifier: String) {
        val plannerData = PlannerData(
            prefMealHourList[index],
            prefMealMinuteList[index],
            "",
            getString(R.string.meals_planner, identifier),
            "Bon appetit.."
        )
        lifecycleScope.launch {
            FirebaseService.deletePlannerDataTomorrow(email,plannerData)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        prefMealHourList[tag] = hourOfDay
        prefMealMinuteList[tag] = minute
        when (tag) {
            0 -> binding.mealText1.text =
                getString(R.string.pref_meal1, prefMealHourList[tag], prefMealMinuteList[tag])
            1 -> binding.mealText2.text =
                getString(R.string.pref_meal2, prefMealHourList[tag], prefMealMinuteList[tag])
            2 -> binding.mealText3.text =
                getString(R.string.pref_meal3, prefMealHourList[tag], prefMealMinuteList[tag])
            3 -> binding.mealText4.text =
                getString(R.string.pref_meal4, prefMealHourList[tag], prefMealMinuteList[tag])
            4 -> binding.mealText5.text =
                getString(R.string.pref_meal5, prefMealHourList[tag], prefMealMinuteList[tag])
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

    private fun setViews() {
        binding.apply {
            mealText1.text =
                getString(R.string.pref_meal1, prefMealHourList[0], prefMealMinuteList[0])
            mealText2.text =
                getString(R.string.pref_meal2, prefMealHourList[1], prefMealMinuteList[1])
            mealText3.text =
                getString(R.string.pref_meal3, prefMealHourList[2], prefMealMinuteList[2])
            mealText4.text
            getString(R.string.pref_meal4, prefMealHourList[3], prefMealMinuteList[3])
            mealText5.text =
                getString(R.string.pref_meal5, prefMealHourList[4], prefMealMinuteList[4])

            layout2.isVisible = numberOfMeals > 1
            layout3.isVisible = numberOfMeals > 2
            layout4.isVisible = numberOfMeals > 3
            layout5.isVisible = numberOfMeals > 4
        }
    }

    private fun snackBar(index: Int, view: View, textView: TextView, stringID: Int) {
        val hour = eatingInfo.mealHour[index]
        val minute = eatingInfo.mealMinute[index]
        val snackBar = Snackbar.make(
            binding.mainLayout,
            "The sleep cycle has been removed",
            Snackbar.LENGTH_LONG
        )

        snackBar.setAction(
            "UNDO",
            MyUndoListener(
                view, hour, minute, index, textView, stringID
            )
        )

        snackBar.show()
    }

    inner class MyUndoListener(
        val view: View,
        private val hourMeal: Int,
        private val minuteMeal: Int,
        private val index: Int,
        private val mealText: TextView,
        private val stringID: Int
    ) : View.OnClickListener {

        override fun onClick(v: View) {
            view.isVisible = true

            prefMealHourList[index] = hourMeal
            prefMealMinuteList[index] = minuteMeal

            mealText.text = getString(
                stringID,
                prefMealHourList[index],
                prefMealMinuteList[index]
            )
        }
    }
}