package gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.databinding.FragmentEatingMainBinding
import gleb.apps.pyhie.mainHabits.sleepingHabit.*
import gleb.apps.pyhie.pojos.*
import gleb.apps.pyhie.util.*
import kotlinx.android.synthetic.main.eating_item.*
import java.util.*

class EatingMain : Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentEatingMainBinding
    private lateinit var viewModel: EatingViewModel
    private lateinit var viewModelFactory: EatingViewModelFactory
    private lateinit var viewPager: ViewPager2
    private lateinit var eatingInfo: EatingInfo
    private val args: EatingMainArgs by navArgs()
    private var hour = 0
    private var minute = 0
    private var numberOfMeals = 1
    private var userPoints = UserPoints()
    private var eatingSavedState: EatingSavedState = EatingSavedState()
    private var submitted = false
    private var email = ""
    private lateinit var sharedPref: SharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eatingInfo = args.eatingInfo
        numberOfMeals = eatingInfo.numberOfMeals
        sharedPref = SharedPref(requireActivity())
        getCurrentState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveCurrentState()
    }

    override fun onPause() {
        super.onPause()
        saveCurrentState()

    }

    override fun onResume() {
        super.onResume()
        getCurrentState()
        resetHabit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        email = sharedPref.getString(Keys.EMAIL)!!
        viewModelFactory = EatingViewModelFactory(email)
        viewModel = ViewModelProvider(this, viewModelFactory)[EatingViewModel::class.java]

        binding = FragmentEatingMainBinding.inflate(inflater, container, false).apply {
            eatingViewModel = viewModel
            savedState = eatingSavedState

            eatTimeButton.setOnClickListener { openTimePicker() }
            healthyButton.setOnClickListener { submitHealthiness(UserConstants.HEALTHY) }
            partHealthyButton.setOnClickListener { submitHealthiness(UserConstants.PART_HEALTHY) }
            partUnhealthyButton.setOnClickListener { submitHealthiness(UserConstants.PART_UNHEALTHY) }
            unhealthyButton.setOnClickListener { submitHealthiness(UserConstants.UNHEALTHY) }
            overeatenButton.setOnClickListener {
                submitHowMuchYouEat(overeatenButton.text.toString().toLowerCase(Locale.ROOT))
            }
            justEnoughButton.setOnClickListener {
                submitHowMuchYouEat(justEnoughButton.text.toString().toLowerCase(Locale.ROOT))
            }
            notEnoughButton.setOnClickListener {
                submitHowMuchYouEat(notEnoughButton.text.toString().toLowerCase(Locale.ROOT))
            }
            undoButton.setOnClickListener { undo() }
            howItWorks.setOnClickListener {findNavController().navigate( EatingMainDirections.actionEatingMainToHowItWorksFragment("EatingHabit")) }
        }
        binding.lifecycleOwner = this

        viewModel.getUserPoints.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            userPoints = it
        })

        submitHabit()
        setViewPager()
        floatingButtons()
        mealNotificationReminder()

        return binding.root
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
        eatingSavedState.minuteList[eatingSavedState.currentMealTag] = minute
        eatingSavedState.hourList[eatingSavedState.currentMealTag] = hourOfDay
        binding.apply {
            when (eatingSavedState.currentRating) {
                "Meal1" -> {
                    onTimeSubmitted(eatTimeButton, healthyLayout, mealText1, mealTextPref1)
                }
                "Meal2" -> {
                    onTimeSubmitted(eatTimeButton, healthyLayout, mealText2, mealTextPref2)
                }
                "Meal3" -> {
                    onTimeSubmitted(eatTimeButton, healthyLayout, mealText3, mealTextPref3)
                }
                "Meal4" -> {
                    onTimeSubmitted(eatTimeButton, healthyLayout, mealText4, mealTextPref4)
                }
                "Meal5" -> {
                    onTimeSubmitted(eatTimeButton, healthyLayout, mealText5, mealTextPref5)
                }
            }
        }
    }

    private fun submitHealthiness(healthiness: String) {
        binding.apply {
            when (eatingSavedState.currentRating) {
                "Meal1" -> {
                    eatingSavedState.healthinessList[0] = healthiness
                    mealText1.text = getString(R.string.would_you_say)
                    onSubmitHealth(healthyLayout, howMuchEatLayout)
                }
                "Meal2" -> {
                    eatingSavedState.healthinessList[1] = healthiness
                    mealText2.text = getString(R.string.would_you_say)
                    onSubmitHealth(healthyLayout, howMuchEatLayout)
                }
                "Meal3" -> {
                    eatingSavedState.healthinessList[2] = healthiness
                    mealText3.text = getString(R.string.would_you_say)
                    onSubmitHealth(healthyLayout, howMuchEatLayout)
                }
                "Meal4" -> {
                    eatingSavedState.healthinessList[3] = healthiness
                    mealText4.text = getString(R.string.would_you_say)
                    onSubmitHealth(healthyLayout, howMuchEatLayout)
                }
                "Meal5" -> {
                    eatingSavedState.healthinessList[4] = healthiness
                    mealText5.text = getString(R.string.would_you_say)
                    onSubmitHealth(healthyLayout, howMuchEatLayout)
                }
            }
        }
    }

    private fun submitHowMuchYouEat(howMuchYouEat: String) {
        binding.apply {
            when (eatingSavedState.currentRating) {
                "Meal1" -> {
                    onSubmitHowMuchYouEat(
                        mealText1,
                        howMuchYouEat,
                        meal2Layout,
                        1,
                        "Meal2",
                        R.string.meal_one_button
                    )
                    mealText2.text = eatingSavedState.meal2Text
                    undoButton.visibility = View.VISIBLE
                }
                "Meal2" -> {
                    onSubmitHowMuchYouEat(
                        mealText2,
                        howMuchYouEat,
                        meal3Layout,
                        2,
                        "Meal3",
                        R.string.meal_two_button
                    )
                    mealText3.text = eatingSavedState.meal3Text
                }
                "Meal3" -> {
                    onSubmitHowMuchYouEat(
                        mealText3,
                        howMuchYouEat,
                        meal4Layout,
                        3,
                        "Meal4",
                        R.string.meal_three_button
                    )
                    mealText4.text = eatingSavedState.meal4Text
                }
                "Meal4" -> {
                    onSubmitHowMuchYouEat(
                        mealText4,
                        howMuchYouEat,
                        meal5Layout,
                        4,
                        "Meal5",
                        R.string.meal_four_button
                    )
                    mealText5.text = eatingSavedState.meal5Text
                }
                "Meal5" -> {
                    onSubmitHowMuchYouEat(
                        mealText5,
                        howMuchYouEat,
                        meal5Layout,
                        5,
                        "Submit",
                        R.string.meal_fifth_button
                    )
                }
            }
        }
    }

    private fun onTimeSubmitted(button: Button, healthLayout: LinearLayout, mealText: TextView, prefMeal: TextView) {
        button.visibility = View.GONE
        healthLayout.visibility = View.VISIBLE
        prefMeal.visibility = View.GONE
        mealText.text = getString(R.string.how_healthy)
    }

    private fun onSubmitHealth(healthLayout: LinearLayout, howMuchYouEat: LinearLayout) {
        healthLayout.visibility = View.GONE
        howMuchYouEat.visibility = View.VISIBLE
    }

    private fun onSubmitHowMuchYouEat(
        mealText: TextView,
        howMuchYouEat: String,
        nextMealLayout: ConstraintLayout,
        mealNumber: Int,
        currentRatingString: String,
        stringId: Int
    ) {
        mealText.text = getString(
            stringId,
            eatingSavedState.hourList[eatingSavedState.currentMealTag],
            eatingSavedState.minuteList[eatingSavedState.currentMealTag],
            eatingSavedState.healthinessList[eatingSavedState.currentMealTag],
            howMuchYouEat
        )
        mealText.setTextColor(Color.BLACK)
        mealText.translationY = 0f
        binding.howMuchEatLayout.visibility = View.GONE
        binding.eatTimeButton.visibility = View.VISIBLE

        if (numberOfMeals > mealNumber) {
            nextMealLayout.visibility = View.VISIBLE
            eatingSavedState.currentRating = currentRatingString
            eatingSavedState.currentMealTag = mealNumber
        } else {
            enableSubmitButton()
            eatingSavedState.currentRating = "Submit"
            binding.ratingsLayout.visibility = View.GONE
            eatingSavedState.ratingsLayoutVisibility = View.GONE
        }
    }


    private fun enableSubmitButton() {
        Handler(Looper.myLooper()!!).postDelayed(700) {
            binding.submitButton.translationY = 200f
            binding.submitButton.visibility = View.VISIBLE
            val animation = binding.submitButton.animate()
            animation.translationY(0f)
            animation.duration = 200
            animation.start()
        }
    }

    private fun submitHabit() {
        binding.submitButton.setOnClickListener {
            submitted = true
            activateFirstChallenge()
            val newEatData = EatingData(
                eatingSavedState.hourList,
                eatingSavedState.minuteList,
                eatingSavedState.healthinessList,
                eatingInfo
            )
            eatingInfo.submitted = true
            eatingInfo.totalPoints = eatingInfo.totalPoints + newEatData.getPoints()
            eatingInfo.numberOfSubmits = eatingInfo.numberOfSubmits + 1
            val eatBalance = (eatingInfo.totalPoints/eatingInfo.numberOfSubmits)*10
            sharedPref.saveInt(Keys.EAT_BALANCE,eatBalance.toInt())
            viewModel.insertEatingInfo(eatingInfo)
            viewModel.insertEatingData(newEatData)
            findNavController().navigate(
                EatingMainDirections.actionEatingMainToHabitIsCompleted(
                    getString(R.string.eating_habit),
                    newEatData.getPoints().toFloat()
                )
            )
            sharedPref.saveBoolean(Keys.EAT1_NOT_SUBMITTED,false)
            if (numberOfMeals>1) sharedPref.saveBoolean(Keys.EAT2_NOT_SUBMITTED,false)
            if (numberOfMeals>2) sharedPref.saveBoolean(Keys.EAT3_NOT_SUBMITTED,false)
            if (numberOfMeals>3) sharedPref.saveBoolean(Keys.EAT4_NOT_SUBMITTED,false)
            if (numberOfMeals>4) sharedPref.saveBoolean(Keys.EAT5_NOT_SUBMITTED,false)
        }
    }

    private fun resetHabit() {
        val reset = ResetHabit(eatingInfo.currentDay,eatingInfo.currentYear)
        if (reset.isNextDay()) {
            eatingInfo.currentDay = GetDates().today
            eatingInfo.currentYear = GetDates().year
            binding.ratingsLayout.visibility = View.VISIBLE
            binding.eatTimeButton.visibility = View.VISIBLE
            binding.mealText1.text = EatingSavedState().meal1Text
            binding.mealTextPref1.visibility = View.VISIBLE
            sharedPref.saveBoolean(Keys.EAT1_NOT_SUBMITTED,true)
            sharedPref.saveBoolean(Keys.EAT2_NOT_SUBMITTED,true)
            sharedPref.saveBoolean(Keys.EAT3_NOT_SUBMITTED,true)
            sharedPref.saveBoolean(Keys.EAT4_NOT_SUBMITTED,true)
            sharedPref.saveBoolean(Keys.EAT5_NOT_SUBMITTED,true)
            if (!eatingInfo.submitted) {
                val penaltyDialog  = PenaltyFragment()
                penaltyDialog.show(parentFragmentManager,PenaltyFragment.TAG)

            } else {
                eatingInfo.submitted = false
            }
            viewModel.insertEatingInfo(eatingInfo)
        }
    }

    private fun setViewPager() {
        viewPager = binding.viewPager
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        val pagerAdapter = ScreenSlidePagerAdapterEating(this)
        viewPager.adapter = pagerAdapter
    }

    private fun floatingButtons() {
        var rotate = false
        binding.apply {
            ViewAnimation().init(addMore)
            ViewAnimation().init(howItWorks)

            fab.setOnClickListener {
                rotate = ViewAnimation().rotateFab(it, !rotate)
                if (rotate) {
                    ViewAnimation().showIn(addMore)
                    ViewAnimation().showIn(howItWorks)
                } else {
                    ViewAnimation().showOut(addMore)
                    ViewAnimation().showOut(howItWorks)
                }
            }
            addMore.setOnClickListener {
                findNavController().navigate(
                    EatingMainDirections.actionEatingMainToChangeEatTimesFragment(
                        eatingInfo
                    )
                )
            }
        }
    }

    private fun undo() {
        Log.d("TAG", "undo: ${eatingSavedState.currentRating} ")
        binding.apply {
            when (eatingSavedState.currentRating) {
                "Meal2" -> {
                    undoMeal2()
                }
                "Meal3" -> {
                    undoMeal3()
                }
                "Meal4" -> {
                    undoMeal4()
                }
                "Meal5" -> {
                    undoMeal5()
                }
                "Submit" -> {
                    when (numberOfMeals) {
                        1 -> {
                            undoMeal2()
                            onUndoSubmitted()
                        }
                        2 -> {
                            undoMeal3()
                            onUndoSubmitted()
                        }
                        3 -> {
                            undoMeal4()
                            onUndoSubmitted()
                        }
                        4 -> {
                            undoMeal5()
                            onUndoSubmitted()
                        }
                        5 -> {
                            mealText5.text = EatingSavedState().meal5Text
                            mealText5.setTextColor(Color.GRAY)
                            onUndoSubmitted()
                        }
                    }
                }
            }
        }
    }

    private fun undoMeal2() {
        binding.apply {
            mealText1.text = EatingSavedState().meal1Text
            mealText1.setTextColor(Color.GRAY)
            mealTextPref1.visibility = View.VISIBLE
            meal2Layout.visibility = View.GONE
            eatingSavedState.currentRating = "Meal1"
            undoButton.visibility = View.GONE
            howMuchEatLayout.visibility = View.GONE
            healthyLayout.visibility = View.GONE
            eatTimeButton.visibility = View.VISIBLE
        }
    }

    private fun undoMeal3() {
        binding.apply {
            mealText2.text = EatingSavedState().meal2Text
            mealText2.setTextColor(Color.GRAY)
            meal3Layout.visibility = View.GONE
            mealTextPref2.visibility = View.VISIBLE
            eatingSavedState.currentRating = "Meal2"
            howMuchEatLayout.visibility = View.GONE
            healthyLayout.visibility = View.GONE
            eatTimeButton.visibility = View.VISIBLE
        }
    }

    private fun undoMeal4() {
        binding.apply {
            mealText3.text = EatingSavedState().meal3Text
            mealText3.setTextColor(Color.GRAY)
            mealTextPref3.visibility = View.VISIBLE
            meal4Layout.visibility = View.GONE
            howMuchEatLayout.visibility = View.GONE
            healthyLayout.visibility = View.GONE
            eatTimeButton.visibility = View.VISIBLE
            eatingSavedState.currentRating = "Meal3"
        }
    }

    private fun undoMeal5() {
        binding.apply {
            mealText4.text = EatingSavedState().meal4Text
            mealText4.setTextColor(Color.GRAY)
            mealTextPref4.visibility = View.VISIBLE
            meal5Layout.visibility = View.GONE
            binding.howMuchEatLayout.visibility = View.GONE
            binding.healthyLayout.visibility = View.GONE
            binding.eatTimeButton.visibility = View.VISIBLE
            eatingSavedState.currentRating = "Meal4"
        }
    }

    private fun onUndoSubmitted() {
        binding.howMuchEatLayout.visibility = View.GONE
        binding.healthyLayout.visibility = View.GONE
        binding.eatTimeButton.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
        binding.ratingsLayout.visibility = View.VISIBLE
    }

    private fun saveCurrentState() {
        if (submitted) {
            eatingSavedState = EatingSavedState(eatingInfo)
            eatingSavedState.meal1Text = getString(R.string.main_habit_is_completed, "Eating habit")
            eatingSavedState.prefTextVisibility1 = View.GONE
            eatingSavedState.ratingsLayoutVisibility = View.GONE

        } else {
            binding.apply {
                eatingSavedState.meal1Text = mealText1.text.toString()
                eatingSavedState.meal2Text = mealText2.text.toString()
                eatingSavedState.meal3Text = mealText3.text.toString()
                eatingSavedState.meal4Text = mealText4.text.toString()
                eatingSavedState.meal5Text = mealText5.text.toString()
                eatingSavedState.mealColor1 = mealText1.currentTextColor
                eatingSavedState.mealColor2 = mealText2.currentTextColor
                eatingSavedState.mealColor3 = mealText3.currentTextColor
                eatingSavedState.mealColor4 = mealText4.currentTextColor
                eatingSavedState.mealColor5 = mealText5.currentTextColor
                eatingSavedState.mealVisibility2 = meal2Layout.visibility
                eatingSavedState.mealVisibility3 = meal3Layout.visibility
                eatingSavedState.mealVisibility4 = meal4Layout.visibility
                eatingSavedState.mealVisibility5 = meal5Layout.visibility
                eatingSavedState.ratingsLayoutVisibility = ratingsLayout.visibility
                eatingSavedState.prefTextVisibility1 = mealTextPref1.visibility
                eatingSavedState.prefTextVisibility2 = mealTextPref2.visibility
                eatingSavedState.prefTextVisibility3 = mealTextPref3.visibility
                eatingSavedState.prefTextVisibility4 = mealTextPref4.visibility
                eatingSavedState.prefTextVisibility5 = mealTextPref5.visibility
                eatingSavedState.eatTimeButtonVisibility = eatTimeButton.visibility
                eatingSavedState.submitVisibility = submitButton.visibility
                eatingSavedState.healthRatingVisibility = healthyLayout.visibility
                eatingSavedState.howMuchYouAteVisibility = howMuchEatLayout.visibility
                eatingSavedState.undoVisibility = undoButton.visibility
            }
        }
        val gson = Gson()
        val gsonString = gson.toJson(
            eatingSavedState
        )
        sharedPref.saveString(Keys.EAT_SAVED_STATE, gsonString)
    }

    private fun getCurrentState() {
        val gson = Gson()
        val json = sharedPref.getString(Keys.EAT_SAVED_STATE)
        val type = object : TypeToken<EatingSavedState?>() {}.type
        eatingSavedState = gson.fromJson<EatingSavedState>(json, type)
    }

    private fun activateFirstChallenge () {
        if (sharedPref.getInt(Keys.CURRENT_CHALLENGE) == 0) sharedPref.saveInt(Keys.CURRENT_CHALLENGE,1)
    }

    class ScreenSlidePagerAdapterEating(frag: Fragment) : FragmentStateAdapter(frag) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ThisWeekQuality()
                1 -> LastWeekQuality()
                2 -> OverallEfficiency()
                else -> ThisWeekQuality()
            }
        }
    }

    private fun mealNotificationReminder() {
        val alarmManager = AlarmService(requireContext())
        val meal1Time = getMilllisTime(eatingInfo.mealHour[0],eatingInfo.mealMinute[0])
        val meal2Time = getMilllisTime(eatingInfo.mealHour[1],eatingInfo.mealMinute[1])
        val meal3Time = getMilllisTime(eatingInfo.mealHour[2],eatingInfo.mealMinute[2])
        val meal4Time = getMilllisTime(eatingInfo.mealHour[3],eatingInfo.mealMinute[3])
        val meal5Time = getMilllisTime(eatingInfo.mealHour[4],eatingInfo.mealMinute[4])
        val meal1TimeNextDay = getMilllisTimeNextDay(eatingInfo.mealHour[0],eatingInfo.mealMinute[0])
        val meal2TimeNextDay = getMilllisTimeNextDay(eatingInfo.mealHour[1],eatingInfo.mealMinute[1])
        val meal3TimeNextDay = getMilllisTimeNextDay(eatingInfo.mealHour[2],eatingInfo.mealMinute[2])
        val meal4TimeNextDay = getMilllisTimeNextDay(eatingInfo.mealHour[3],eatingInfo.mealMinute[3])
        val meal5TimeNextDay = getMilllisTimeNextDay(eatingInfo.mealHour[4],eatingInfo.mealMinute[4])

        if (meal1Time <= System.currentTimeMillis())  alarmManager.setAlarmForNotificationMeal1(meal1TimeNextDay) else{
            alarmManager.setAlarmForNotificationMeal1(meal1Time)
        }

        if(numberOfMeals> 1) {
            if (meal2Time <= System.currentTimeMillis())  alarmManager.setAlarmForNotificationMeal2(meal2TimeNextDay) else {
                alarmManager.setAlarmForNotificationMeal2(meal2Time)
            }

        }
        if(numberOfMeals> 2) {
            if (meal3Time <= System.currentTimeMillis())  alarmManager.setAlarmForNotificationMeal3(meal3TimeNextDay) else {
                alarmManager.setAlarmForNotificationMeal3(meal3Time)
            }
        }
        if(numberOfMeals> 3) {
            if (meal4Time <= System.currentTimeMillis())  alarmManager.setAlarmForNotificationMeal4(meal4TimeNextDay) else alarmManager.setAlarmForNotificationMeal4(meal4Time)
        }
        if(numberOfMeals> 4) {
            if (meal5Time <= System.currentTimeMillis())  alarmManager.setAlarmForNotificationMeal5(meal5TimeNextDay) else alarmManager.setAlarmForNotificationMeal5(meal5Time)
        }
    }

    private fun getMilllisTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            this.set(Calendar.HOUR_OF_DAY, hour)
            this.set(Calendar.MINUTE, minute)
        }
        return calendar.timeInMillis
    }

    private fun getMilllisTimeNextDay(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            this.set(Calendar.DAY_OF_YEAR, this.get(Calendar.DAY_OF_YEAR) +1)
            this.set(Calendar.HOUR_OF_DAY, hour)
            this.set(Calendar.MINUTE, minute)
        }
        return calendar.timeInMillis
    }


}