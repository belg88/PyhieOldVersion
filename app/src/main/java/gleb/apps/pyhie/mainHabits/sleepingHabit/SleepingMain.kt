package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.app.Application
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gleb.apps.pyhie.*
import gleb.apps.pyhie.challenges.ChallengesDialog
import gleb.apps.pyhie.databinding.SleepingMainBinding
import gleb.apps.pyhie.pojos.SleepingData
import gleb.apps.pyhie.pojos.SleepingInfo
import gleb.apps.pyhie.pojos.UserPoints
import gleb.apps.pyhie.util.*
import java.util.*


class SleepingMain : Fragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: SleepingMainBinding
    private lateinit var viewModel_: SleepingViewModel
    private lateinit var viewModelFactory: SleepingViewModelFactory
    private lateinit var viewPager: ViewPager2
    private lateinit var sharedPref: SharedPref
    lateinit var sleepingInfo: SleepingInfo
    private val args: SleepingMainArgs by navArgs()
    private var email = ""
    private var hour = 0
    private var minute = 0
    private var numberOfSleeps = 1
    private val TAG = "TAG"
    private var userPoints = UserPoints()
    private var sleepingSavedState = SleepingSavedState()
    private var submitted = false
    private var wokeUpString = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(requireActivity())
        email = sharedPref.getString(Keys.EMAIL)!!
        sleepingInfo = args.sleepInfo
        numberOfSleeps = sleepingInfo.numberOfSleeps
        getCurrentState()
    }

    override fun onResume() {
        super.onResume()
        getCurrentState()
        resetHabit()
    }

    override fun onPause() {
        super.onPause()
        saveCurrentState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveCurrentState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelFactory =
            SleepingViewModelFactory(
                email,
                Application()
            )
        viewModel_ = ViewModelProvider(this, viewModelFactory)[SleepingViewModel::class.java]

        binding = SleepingMainBinding.inflate(inflater, container, false).apply {
            viewModel = viewModel_
            savedState = sleepingSavedState
            selectTimeButton.setOnClickListener { openTimePicker() }
            submitButton.setOnClickListener { submitHabit() }
            wellSleptButton.setOnClickListener { onRatingSelected(wellSleptButton) }
            asUsualSleptButton.setOnClickListener { onRatingSelected(asUsualSleptButton) }
            badlySleptButton.setOnClickListener { onRatingSelected(badlySleptButton) }
            undoButton.setOnClickListener { undo() }
            howItWorks.setOnClickListener { findNavController().navigate(SleepingMainDirections.actionSleepingMainToHowItWorksFragment("SleepingHabit")) }
        }
        binding.lifecycleOwner = this

        viewModel_.getUserPoints.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            userPoints = it
        })


        setViewPager()
        floatingButtons()
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
        binding.apply {
            when (sleepingSavedState.currentRating) {
                "BedTime1" -> {
                    setBedTime(bedTimeText1, prefBedTimeText1, wakeUpLayout1, hourOfDay, minute)
                    undoButton.visibility = View.VISIBLE
                    sleepingSavedState.currentRating = "WokeUp1"
                    sleepingSavedState.bedTimeHoursList[0] = hourOfDay
                    sleepingSavedState.bedTimeMinutesList[0] = minute
                }
                "WokeUp1" -> {
                    setWokeTime(
                        wakeUpText1,
                        prefWakeTimeText1,
                        selectTimeButton,
                        ratingsLayout,
                        hourOfDay,
                        minute
                    )
                    sleepingSavedState.wokeUpMinutesList[0] = minute
                    sleepingSavedState.wokeUpHoursList[0] = hourOfDay
                    ratingsLayout.visibility = View.VISIBLE
                }
                "BedTime2" -> {
                    setBedTime(bedTimeText2, prefBedTimeText2, wakeUpLayout2, hourOfDay, minute)
                    sleepingSavedState.currentRating = "WokeUp2"
                    sleepingSavedState.bedTimeHoursList[1] = hourOfDay
                    sleepingSavedState.bedTimeMinutesList[1] = minute
                }
                "WokeUp2" -> {
                    setWokeTime(
                        wakeUpText2,
                        prefWakeTimeText2,
                        selectTimeButton,
                        ratingsLayout,
                        hourOfDay,
                        minute
                    )
                    sleepingSavedState.wokeUpMinutesList[1] = minute
                    sleepingSavedState.wokeUpHoursList[1] = hourOfDay
                    ratingsLayout.visibility = View.VISIBLE
                }
                "BedTime3" -> {
                    setBedTime(bedTimeText3, prefBedTimeText3, wakeUpLayout3, hourOfDay, minute)
                    sleepingSavedState.currentRating = "WokeUp3"
                    sleepingSavedState.bedTimeHoursList[2] = hourOfDay
                    sleepingSavedState.bedTimeMinutesList[2] = minute
                }
                "WokeUp3" -> {
                    setWokeTime(
                        wakeUpText3,
                        prefWakeTimeText3,
                        selectTimeButton,
                        ratingsLayout,
                        hourOfDay,
                        minute
                    )
                    sleepingSavedState.wokeUpMinutesList[2] = minute
                    sleepingSavedState.wokeUpHoursList[2] = hourOfDay
                    ratingsLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setBedTime(
        bedTimeText: TextView,
        prefText: TextView,
        nextLayout: ConstraintLayout,
        hourOfDay: Int,
        minute: Int
    ) {
        bedTimeText.text = getString(R.string.went_to_bed1, hourOfDay, minute)
        bedTimeText.setTextColor(Color.BLACK)
        prefText.visibility = View.GONE
        nextLayout.visibility = View.VISIBLE
    }

    private fun setWokeTime(
        wakeTimeText: TextView,
        prefText: TextView,
        timeButton: Button,
        ratingsLayout: LinearLayout,
        hourOfDay: Int,
        minute: Int
    ) {
        wakeTimeText.text = getString(R.string.would_you_say)
        wokeUpString = getString(R.string.woke_up1, hourOfDay, minute)
        prefText.visibility = View.GONE
        ratingsLayout.visibility = View.VISIBLE
        timeButton.visibility = View.GONE
    }

    private fun onRatingSelected(button: Button) {
        binding.apply {
            when (sleepingSavedState.currentRating) {
                "WokeUp1" -> {
                    sleepingSavedState.currentRating =
                        if (numberOfSleeps > 1) "BedTime2" else "Submit"
                    bedTimeLayout2.visibility = if (numberOfSleeps > 1) View.VISIBLE else View.GONE
                    selectTimeButton.visibility =
                        if (numberOfSleeps > 1) View.VISIBLE else View.GONE
                    submitButton.visibility = if (numberOfSleeps > 1) View.GONE else View.VISIBLE
                    setRatings(wakeUpText1, ratingsLayout, button)
                }
                "WokeUp2" -> {
                    sleepingSavedState.currentRating =
                        if (numberOfSleeps > 2) "BedTime3" else "Submit"
                    bedTimeLayout3.visibility = if (numberOfSleeps > 2) View.VISIBLE else View.GONE
                    selectTimeButton.visibility =
                        if (numberOfSleeps > 2) View.VISIBLE else View.GONE
                    submitButton.visibility = if (numberOfSleeps > 2) View.GONE else View.VISIBLE
                    setRatings(wakeUpText2, ratingsLayout, button)
                }
                "WokeUp3" -> {
                    sleepingSavedState.currentRating = "Submit"
                    submitButton.visibility = View.VISIBLE
                    setRatings(wakeUpText3, ratingsLayout, button)
                }
            }
        }
    }

    private fun setRatings(textView: TextView, ratingsLayout: LinearLayout, button: Button) {
        textView.text = wokeUpString + " " + button.text
        textView.setTextColor(Color.BLACK)
        ratingsLayout.visibility = View.GONE
    }

    private fun setViewPager() {
        viewPager = binding.viewPager
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
    }

    private fun submitHabit() {
        submitted = true
        activateFirstChallenge()
        val newSleepData = SleepingData(
            sleepingSavedState.wokeUpHoursList,
            sleepingSavedState.wokeUpMinutesList,
            sleepingSavedState.bedTimeHoursList,
            sleepingSavedState.bedTimeMinutesList,
            sleepingInfo
        )

        sleepingInfo.isSubmitted = true
        sleepingInfo.currentDay = GetDates().today
        sleepingInfo.currentYear = GetDates().year
        sleepingInfo.numberOfSubmits = sleepingInfo.numberOfSubmits + 1
        sleepingInfo.totalPointsEarned = sleepingInfo.totalPointsEarned + newSleepData.getPoints()
        val sleepBalance = (sleepingInfo.totalPointsEarned/sleepingInfo.numberOfSubmits)*10
        sharedPref.saveInt(Keys.SLEEP_BALANCE,sleepBalance.toInt())
        viewModel_.insertSleepInfo(sleepingInfo)
        viewModel_.insertSleepData(newSleepData)


        findNavController().navigate(
            SleepingMainDirections.actionSleepingMainToHabitIsCompleted(
                getString(R.string.sleeping_habit),
                newSleepData.getPoints().toFloat()
            )
        )


        SharedPref(requireActivity()).saveBoolean(Keys.SLEEP1_NOT_SUBMITTED, false)
        if (numberOfSleeps>1) SharedPref(requireActivity()).saveBoolean(Keys.SLEEP2_NOT_SUBMITTED, false)
        if (numberOfSleeps>2) SharedPref(requireActivity()).saveBoolean(Keys.SLEEP3_NOT_SUBMITTED, false)
    }

    private fun resetHabit() {
        val reset = ResetHabit(sleepingInfo.currentDay,sleepingInfo.currentYear)
        if (reset.isNextDay()) {
            sleepingInfo.currentDay = GetDates().today
            sleepingInfo.currentYear = GetDates().year
            binding.bedTimeText1.text = SleepingSavedState().bedTimeText1
            binding.selectTimeButton.visibility = View.VISIBLE
            binding.prefBedTimeText1.visibility = View.VISIBLE
            SharedPref(requireActivity()).saveBoolean(Keys.SLEEP1_NOT_SUBMITTED, true)
            SharedPref(requireActivity()).saveBoolean(Keys.SLEEP2_NOT_SUBMITTED, true)
            SharedPref(requireActivity()).saveBoolean(Keys.SLEEP3_NOT_SUBMITTED, true)
            if (!sleepingInfo.isSubmitted) {
                val penaltyDialog  = PenaltyFragment()
                penaltyDialog.show(parentFragmentManager,PenaltyFragment.TAG)

            } else {
                sleepingInfo.isSubmitted = false
            }
            viewModel_.insertSleepInfo(sleepingInfo)
        }
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
                    SleepingMainDirections.actionSleepingMainToChangeSleepTimes(
                        sleepingInfo
                    )
                )
            }
        }
    }

    private fun undo() {
        binding.apply {
            when (sleepingSavedState.currentRating) {
                "WokeUp1" -> {
                    onUndo(
                        bedTimeText1,
                        wakeUpLayout1,
                        prefBedTimeText1,
                        false,
                        "BedTime1",
                        wakeUpText1
                    )
                    undoButton.visibility = View.GONE
                }
                "WokeUp2" -> onUndo(
                    bedTimeText2,
                    wakeUpLayout2,
                    prefBedTimeText2,
                    false,
                    "BedTime2",
                    wakeUpText2
                )
                "WokeUp3" -> onUndo(
                    bedTimeText3,
                    wakeUpLayout3,
                    prefBedTimeText3,
                    false,
                    "BedTime3",
                    wakeUpText3
                )
                "BedTime2" -> onUndo(
                    wakeUpText1,
                    bedTimeLayout2,
                    prefWakeTimeText1,
                    true,
                    "WokeUp1",
                    wakeUpText1
                )
                "BedTime3" -> onUndo(
                    wakeUpText2,
                    bedTimeLayout3,
                    prefWakeTimeText2,
                    true,
                    "WokeUp2",
                    wakeUpText2
                )
                "Submit" -> {
                    when (numberOfSleeps) {
                        1 -> onUndoSubmit(wakeUpText1, "WokeUp1", prefWakeTimeText1)
                        2 -> onUndoSubmit(wakeUpText2, "WokeUp2", prefWakeTimeText2)
                        3 -> onUndoSubmit(wakeUpText3, "WokeUp3", prefWakeTimeText3)
                    }
                }
            }
        }
    }

    private fun onUndo(
        sleepText: TextView,
        currentSleepLayout: ConstraintLayout,
        prefText: TextView,
        isBedTime: Boolean,
        currentRating: String,
        wokeTextView: TextView
    ) {
        sleepText.text =
            if (isBedTime) getString(R.string.what_time_did_you_woke_up) else getString(R.string.what_time_did_you_go_to_bed)
        sleepText.setTextColor(Color.GRAY)
        currentSleepLayout.visibility = View.GONE
        prefText.visibility = View.VISIBLE
        binding.ratingsLayout.visibility = View.GONE
        binding.selectTimeButton.visibility = View.VISIBLE
        sleepingSavedState.currentRating = currentRating
        wokeTextView.text = getString(R.string.what_time_did_you_woke_up)
    }

    private fun onUndoSubmit(sleepText: TextView, currentRating: String, prefText: TextView) {
        sleepText.text = getString(R.string.what_time_did_you_woke_up)
        sleepText.setTextColor(Color.GRAY)
        prefText.visibility = View.VISIBLE
        binding.submitButton.visibility = View.GONE
        binding.selectTimeButton.visibility = View.VISIBLE
        sleepingSavedState.currentRating = currentRating
    }

    private fun saveCurrentState() {
        if (submitted) {
            sleepingSavedState = SleepingSavedState(sleepingInfo)
            sleepingSavedState.bedTimeText1 = getString(R.string.main_habit_is_completed, "Sleeping habit")
            sleepingSavedState.selectButtonVisibility = View.GONE
            sleepingSavedState.prefBedVisibility1 = View.GONE
        } else {
            binding.apply {
                sleepingSavedState.bedTextColor1 = bedTimeText1.currentTextColor
                sleepingSavedState.bedTextColor2 = bedTimeText2.currentTextColor
                sleepingSavedState.bedTextColor3 = bedTimeText3.currentTextColor
                sleepingSavedState.wokeTextColor1 = wakeUpText1.currentTextColor
                sleepingSavedState.wokeTextColor2 = wakeUpText2.currentTextColor
                sleepingSavedState.wokeTextColor3 = wakeUpText3.currentTextColor
                sleepingSavedState.bedTimeText1 = bedTimeText1.text.toString()
                sleepingSavedState.bedTimeText2 = bedTimeText2.text.toString()
                sleepingSavedState.bedTimeText3 = bedTimeText3.text.toString()
                sleepingSavedState.wokeUpTimeText1 = wakeUpText1.text.toString()
                sleepingSavedState.wokeUpTimeText2 = wakeUpText2.text.toString()
                sleepingSavedState.wokeUpTimeText3 = wakeUpText3.text.toString()
                sleepingSavedState.ratingsLayoutVisibility = ratingsLayout.visibility
                sleepingSavedState.selectButtonVisibility = selectTimeButton.visibility
                sleepingSavedState.submitVisibility = submitButton.visibility
                sleepingSavedState.prefBedVisibility1 = prefBedTimeText1.visibility
                sleepingSavedState.prefBedVisibility2 = prefBedTimeText2.visibility
                sleepingSavedState.prefBedVisibility3 = prefBedTimeText3.visibility
                sleepingSavedState.prefWokeUpVisibility1 = prefWakeTimeText1.visibility
                sleepingSavedState.prefWokeUpVisibility2 = prefWakeTimeText2.visibility
                sleepingSavedState.prefWokeUpVisibility3 = prefWakeTimeText3.visibility
                sleepingSavedState.bedLayoutVisibility2 = bedTimeLayout2.visibility
                sleepingSavedState.bedLayoutVisibility3 = bedTimeLayout3.visibility
                sleepingSavedState.wokeUpLayoutVisibility1 = wakeUpLayout1.visibility
                sleepingSavedState.wokeUpLayoutVisibility2 = wakeUpLayout2.visibility
                sleepingSavedState.wokeUpLayoutVisibility3 = wakeUpLayout3.visibility
                sleepingSavedState.undoVisibility = undoButton.visibility
            }
        }
        val gson = Gson()
        val gsonString = gson.toJson(
            sleepingSavedState
        )
        sharedPref.saveString(Keys.SLEEP_SAVED_STATE, gsonString)
    }

    private fun getCurrentState() {
        val gson = Gson()
        val json = sharedPref.getString(Keys.SLEEP_SAVED_STATE)
        val type = object : TypeToken<SleepingSavedState?>() {}.type
        sleepingSavedState = gson.fromJson<SleepingSavedState>(json, type)
    }

    private fun activateFirstChallenge () {
        if (sharedPref.getInt(Keys.CURRENT_CHALLENGE) == 0) sharedPref.saveInt(Keys.CURRENT_CHALLENGE,1)
    }

    class ScreenSlidePagerAdapter(frag: Fragment) : FragmentStateAdapter(frag) {
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
}


