package gleb.apps.pyhie.intro

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.animation.CustomAnimations
import gleb.apps.pyhie.databinding.FragmentIntroThirdPartBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.mainHabits.sleepingHabit.SleepingSavedState
import gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit.CleaningInfo
import gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit.CleaningSavedState
import gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit.EatingSavedState
import gleb.apps.pyhie.mainHabits.sleepingHabit.planningHabit.PlannerInfo
import gleb.apps.pyhie.pojos.EatingInfo
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingInfo
import gleb.apps.pyhie.util.GetDates
import gleb.apps.pyhie.util.Keys
import kotlinx.android.synthetic.main.sleeping_main.*
import kotlinx.coroutines.launch
import java.util.*


class IntroThirdPart : Fragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentIntroThirdPartBinding
    private val args: IntroThirdPartArgs by navArgs()
    var hour = 0
    var minute = 0
    var timeString = ""
    private var sleepInfo = SleepingInfo()
    private var eatingInfo = EatingInfo()
    private var bedTime = 0.0
    private var wokeTime = 0.0
    private var mealTime1 = 0.0
    private var mealTime2 = 0.0
    private var mealTime3 = 0.0
    private var email = ""
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = args.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroThirdPartBinding.inflate(inflater, container, false)
        binding.apply {
            okButton.setOnClickListener { ok() }
            bedTimeButton.setOnClickListener {
                timeString = "Bedtime"
                openTimePicker()
            }
            wokeTimeButton.setOnClickListener {
                openTimePicker()
            }
            finishButton.setOnClickListener {
                if (timeString == "") {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.bedTimeLayout.visibility = View.VISIBLE
                    binding.wokeTimeLayout.visibility = View.GONE
                    binding.finishButton.visibility = View.GONE

                } else {
                    wokeTimeLayout.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    initMainHabits()
                }
            }
            title.text = getString(R.string.welcome_to_pyhie, name)
        }
        email = SharedPref(requireContext()).getString(Keys.EMAIL)!!

        return binding.root
    }

    private fun ok() {
        binding.apply {
            CustomAnimations.slideUpOffTheScreen(welcomeLayout, -1900f, 1000, 0)
            CustomAnimations.slideUpToTheScreen(bedTimeLayout, 1000, 1900f, 500)
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
        when (timeString) {
            "Bedtime" -> {
                bedTime = hourOfDay.toDouble() + minute.toDouble() / 60
                if (bedTime > 6 && bedTime < 17) {
                    Toast.makeText(
                        requireContext(),
                        "Please select evening or night times.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sleepInfo.prefBedTimeHours[0] = hourOfDay
                    sleepInfo.prefBedTimeMinutes[0] = minute
                    binding.bedTimeLayout.visibility = View.GONE
                    binding.wokeTimeLayout.visibility = View.VISIBLE
                    timeString = "WokeTime"
                }
            }
            "WokeTime" -> {
                wokeTime = hourOfDay.toDouble() + minute.toDouble() / 60
                if (wokeTime > 12) {
                    Toast.makeText(
                        requireContext(),
                        "Please select morning time.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (bedTime < 6 && bedTime > wokeTime) {
                    Toast.makeText(
                        requireContext(),
                        "Waking up time must be after bed time. ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sleepInfo.prefWokeUpHours[0] = hourOfDay
                    sleepInfo.prefWokeUpMinutes[0] = minute
                    binding.wokeTitle1.text = getString(R.string.eat1)
                    timeString = "Meal1"
                }
            }
            "Meal1" -> {
                mealTime1 = hourOfDay.toDouble() + minute.toDouble() / 60
                if (mealTime1 < wokeTime) {
                    Toast.makeText(
                        requireContext(),
                        "Meal time must be after woke up time",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (bedTime > wokeTime && mealTime1 > bedTime) {
                    Toast.makeText(
                        requireContext(),
                        "Meal time must be before bed time.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    eatingInfo.mealHour[0] = hourOfDay
                    eatingInfo.mealMinute[0] = minute
                    binding.wokeTitle1.text = getString(R.string.eat2)
                    binding.finishButton.visibility = View.VISIBLE
                    binding.finishButton.text = getString(R.string.finish_text1)
                    timeString = "Meal2"
                }
            }
            "Meal2" -> {
                mealTime2 = hourOfDay.toDouble() + minute.toDouble() / 60
                if (mealTime2 < mealTime1) {
                    Toast.makeText(
                        requireContext(),
                        "Second meal time must come after the first meal time.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (bedTime > wokeTime && mealTime1 > bedTime) {
                    Toast.makeText(
                        requireContext(),
                        "Meal time must be before bed time.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    eatingInfo.mealHour[1] = hourOfDay
                    eatingInfo.mealMinute[1] = minute
                    eatingInfo.numberOfMeals = 2
                    binding.wokeTitle1.text = getString(R.string.eat3)
                    binding.finishButton.text = getString(R.string.finish_text2)
                    timeString = "Meal3"
                }
            }
            "Meal3" -> {
                mealTime3 = hourOfDay.toDouble() + minute.toDouble() / 60
                if (mealTime3 < mealTime2) {
                    Toast.makeText(
                        requireContext(),
                        "Third meal time must come after the second meal time.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (bedTime > wokeTime && mealTime1 > bedTime) {
                    Toast.makeText(
                        requireContext(),
                        "Meal time must be before bed time.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    eatingInfo.mealHour[2] = hourOfDay
                    eatingInfo.mealMinute[2] = minute
                    binding.progressBar.visibility = View.VISIBLE
                    binding.wokeTimeLayout.visibility = View.GONE
                    initMainHabits()
                }
            }
            "" -> {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong, please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.bedTimeLayout.visibility = View.VISIBLE
                binding.wokeTimeLayout.visibility = View.GONE
                binding.finishButton.visibility = View.GONE
            }
        }
    }


    private fun initMainHabits() {
        val eatingSaved = EatingSavedState(eatingInfo)
        val gson = Gson()
        val gsonString = gson.toJson(
            eatingSaved
        )
        SharedPref(requireContext()).saveString(Keys.EAT_SAVED_STATE, gsonString)

        val sleepSaved = SleepingSavedState(sleepInfo)
        val gsonSleepString = gson.toJson(
            sleepSaved
        )
        SharedPref(requireContext()).saveString(Keys.SLEEP_SAVED_STATE, gsonSleepString)

        val savedState = CleaningSavedState()
        val gsonCleanString = gson.toJson(
            savedState
        )
        SharedPref(requireContext()).saveString(Keys.CLEANING_SAVED_STATE, gsonCleanString)

        SharedPref(requireContext()).saveBoolean(Keys.CLEAN_NOT_SUBMITTED, true)
        SharedPref(requireContext()).saveBoolean(Keys.SLEEP1_NOT_SUBMITTED, true)
        SharedPref(requireContext()).saveBoolean(Keys.EAT1_NOT_SUBMITTED, true)
        SharedPref(requireContext()).saveBoolean(Keys.PLANNER_NOT_SUBMITTED, true)
        SharedPref(requireContext()).saveInt(Keys.CURRENT_CHALLENGE, 0)
        SharedPref(requireContext()).saveInt(Keys.CHALLENGE_1_START_DAY, GetDates().today)
        SharedPref(requireContext()).saveBoolean(Keys.CHALLENGE_NOT_SHOWN, true)
        SharedPref(requireContext()).saveBoolean(Keys.ADVICE_NOT_SHOWN, true)
        SharedPref(requireContext()).saveInt(Keys.CURRENT_CHALLENGE, 0)
        SharedPref(requireContext()).saveInt(Keys.SLEEP_BALANCE, 0)
        SharedPref(requireContext()).saveInt(Keys.EAT_BALANCE, 0)
        SharedPref(requireContext()).saveInt(Keys.PLANNER_BALANCE, 0)
        SharedPref(requireContext()).saveInt(Keys.CLEAN_BALANCE, 0)
        SharedPref(requireContext()).saveInt(Keys.ADVICE_DAY_NUMBER, 0)


        lifecycleScope.launch {
            val today = GetDates().today
            val year = GetDates().year
            sleepInfo.currentDay = today
            sleepInfo.currentYear = year
            eatingInfo.currentDay = today
            eatingInfo.currentYear = year
            val cleaningInfo = CleaningInfo(false, today, year)
            val plannerInfo = PlannerInfo(false, today, year)
            FirebaseService.insertSleepingPref(sleepInfo, email)
            FirebaseService.insertEatingInfo(eatingInfo, email)
            FirebaseService.insertCleaningInfo(cleaningInfo, email)
            FirebaseService.insertPlannerInfo(plannerInfo, email)
            insertPlannerData()
        }

        findNavController().navigate(IntroThirdPartDirections.actionIntroThirdPartToMainActivity())
        activity?.finish()
    }

    suspend fun insertPlannerData() {
        if (eatingInfo.numberOfMeals > 0) {
            val plannerData = PlannerData(
                eatingInfo.mealHour[0],
                eatingInfo.mealMinute[0],
                "",
                getString(R.string.meals_planner, "First"),
                "Bon appetit.."
            )
            FirebaseService.insertPlannerDataToday(plannerData, email)
            FirebaseService.insertPlannerDataTomorrow(plannerData, email)
        }

        if (eatingInfo.numberOfMeals > 1) {
            val plannerData = PlannerData(
                eatingInfo.mealHour[1],
                eatingInfo.mealMinute[1],
                "",
                getString(R.string.meals_planner, "Second"),
                "Bon appetit.."
            )
            FirebaseService.insertPlannerDataToday(plannerData, email)
            FirebaseService.insertPlannerDataTomorrow(plannerData, email)
        }

        if (eatingInfo.numberOfMeals > 2) {
            val plannerData = PlannerData(
                eatingInfo.mealHour[1],
                eatingInfo.mealMinute[1],
                "",
                getString(R.string.meals_planner, "Third"),
                "Bon appetit.."
            )
            FirebaseService.insertPlannerDataToday(plannerData, email)
            FirebaseService.insertPlannerDataTomorrow(plannerData, email)
        }

        val bedTimePastMidnight =
            sleepInfo.prefBedTimeHours[0] < sleepInfo.prefWokeUpHours[0]

        val plannerDataWokeUp = PlannerData(
            sleepInfo.prefWokeUpHours[0],
            sleepInfo.prefWokeUpMinutes[0],
            "",
            getString(R.string.woke_up_time_planner, ""),
            "Rise and shine!",
            true, false
        )

        val plannerDataBedTime = PlannerData(
            sleepInfo.prefBedTimeHours[0],
            sleepInfo.prefBedTimeMinutes[0],
            "",
            getString(R.string.bed_time_planner, ""),
            "Sweet dreams...",
            false,
            true,
            bedTimePastMidnight
        )
        FirebaseService.insertPlannerDataToday(plannerDataBedTime, email)
        FirebaseService.insertPlannerDataToday(plannerDataWokeUp, email)
        FirebaseService.insertPlannerDataTomorrow(plannerDataBedTime, email)
        FirebaseService.insertPlannerDataTomorrow(plannerDataWokeUp, email)

    }
}
