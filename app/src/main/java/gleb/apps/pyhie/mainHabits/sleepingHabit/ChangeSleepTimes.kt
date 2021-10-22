package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.app.Application
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.databinding.FragmentChangeSleepTimesBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingInfo
import kotlinx.coroutines.launch
import java.util.*


class ChangeSleepTimes : Fragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentChangeSleepTimesBinding
    private lateinit var viewModel_: SleepingViewModel
    private lateinit var viewModelFactory: SleepingViewModelFactory

    private val args: ChangeSleepTimesArgs by navArgs()
    lateinit var sleepInfo: SleepingInfo
    private var tag = 0
    private var hour = 0
    private var minute = 0
    private var prefBedHourList = mutableListOf(0, 0, 0)
    private var prefBedMinuteList = mutableListOf(0, 0, 0)
    private var prefWokeUpHourList = mutableListOf(0, 0, 0)
    private var prefWokeUpMinutesList = mutableListOf(0, 0, 0)
    private var numberOfSleeps = 1
    private var email = ""
    private var bedSubmitted = true
    private var wokeSubmitted = true
    private var sleepTwoIsValid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = SharedPref(requireActivity()).getString(Keys.EMAIL)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sleepInfo = args.sleepInfo
        viewModelFactory =
            SleepingViewModelFactory(
                email,
                Application()
            )
        viewModel_ = ViewModelProvider(this, viewModelFactory)[SleepingViewModel::class.java]
        binding = FragmentChangeSleepTimesBinding.inflate(inflater, container, false)
        numberOfSleeps = sleepInfo.numberOfSleeps


        binding.apply {
            changeBed1.setOnClickListener {
                tag = 1
                openTimePicker()
            }
            changeWoke1.setOnClickListener {
                tag = 2
                openTimePicker()
            }
            changeBed2.setOnClickListener {
                tag = 3
                openTimePicker()
            }
            changeWoke2.setOnClickListener {
                tag = 4
                openTimePicker()
            }
            changeBed3.setOnClickListener {
                tag = 5
                openTimePicker()
            }
            changeWoke3.setOnClickListener {
                tag = 6
                openTimePicker()
            }

            remove2.setOnClickListener {
                val bedHour = sleepInfo.prefBedTimeHours[1]
                val bedMinute = sleepInfo.prefBedTimeMinutes[1]
                val wokeHour = sleepInfo.prefWokeUpHours[1]
                val wokeMinute = sleepInfo.prefBedTimeMinutes[1]

                submitButton.isEnabled = true
                layout.isVisible = false
                numberOfSleeps = 1
                prefBedHourList[1] = 0
                prefBedMinuteList[1] = 0
                prefWokeUpHourList[1] = 0
                prefWokeUpMinutesList[1] = 0

                val snackBar = Snackbar.make(
                    binding.constraintLayout,
                    "The sleep cycle has been removed",
                    Snackbar.LENGTH_LONG
                )

                snackBar.setAction(
                    "UNDO",
                    MyUndoListener(
                        layout,
                        bedHour,
                        bedMinute,
                        wokeHour,
                        wokeMinute,
                        1,
                        bedText2,
                        wokeText2
                    )
                )

                snackBar.show()
            }

            remove3.setOnClickListener {
                val bedHour =
                    sleepInfo.prefBedTimeHours[2]     // Saving these values because sleepInfo becomes 0 after the remove in here.
                val bedMinute = sleepInfo.prefBedTimeMinutes[2]
                val wokeHour = sleepInfo.prefWokeUpHours[2]
                val wokeMinute = sleepInfo.prefBedTimeMinutes[2]
                submitButton.isEnabled = sleepTwoIsValid
                layout2.isVisible = false
                numberOfSleeps = 2
                prefBedHourList[2] = 0
                prefBedMinuteList[2] = 0
                prefWokeUpHourList[2] = 0
                prefWokeUpMinutesList[2] = 0
                remove2.isVisible = true

                val snackBar = Snackbar.make(
                    binding.constraintLayout,
                    "The sleep cycle has been removed",
                    Snackbar.LENGTH_LONG
                )

                snackBar.setAction(
                    "UNDO",
                    MyUndoListener(
                        layout2,
                        bedHour,
                        bedMinute,
                        wokeHour,
                        wokeMinute,
                        2,
                        bedText3,
                        wokeText3
                    )
                )

                snackBar.show()
            }

            addMore.setOnClickListener {
                submitButton.isEnabled = false
                bedSubmitted = false
                wokeSubmitted = false
                when (numberOfSleeps) {
                    1 -> {
                        layout.isVisible = true
                        numberOfSleeps = 2
                        sleepTwoIsValid = false
                    }
                    2 -> {
                        layout2.isVisible = true
                        numberOfSleeps = 3
                        remove2.isVisible = false
                    }
                    3 -> Toast.makeText(
                        requireContext(),
                        "Max number of sleeps is reached",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            submitButton.setOnClickListener { checkTimesBeforeSubmit() }
        }
        return binding.root
    }

    private fun getWokeSleepPlannerData(index: Int, newSleepInfo: SleepingInfo): PlannerData {
        val sleepNumber = if (index == 1) "for second sleep" else "for third sleep"
        return PlannerData(
            newSleepInfo.prefWokeUpHours[index],
            newSleepInfo.prefWokeUpMinutes[index],
            "",
            getString(R.string.woke_up_time_planner, sleepNumber),
            "Rise and shine!"
        )
    }

    private fun getBedSleepPlannerData(index: Int, newSleepInfo: SleepingInfo): PlannerData {
        val sleepNumber = if (index == 1) "for second sleep" else "for third sleep"
        return PlannerData(
            newSleepInfo.prefBedTimeHours[index],
            newSleepInfo.prefBedTimeMinutes[index],
            "",
            getString(R.string.bed_time_planner, sleepNumber),
            "Sweet dreams..."
        )
    }

    private fun deletePlannerData(index: Int) {
        lifecycleScope.launch {
            FirebaseService.deletePlannerDataToday(
                email,
                getWokeSleepPlannerData(index, sleepInfo)
            )
            FirebaseService.deletePlannerDataToday(
                email,
                getBedSleepPlannerData(index, sleepInfo)
            )
            FirebaseService.deletePlannerDataTomorrow(
                email,
                getWokeSleepPlannerData(index, sleepInfo)
            )
            FirebaseService.deletePlannerDataTomorrow(
                email,
                getBedSleepPlannerData(index, sleepInfo)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        getSleepTimes()
        setViews()
        binding.apply {
            layout.isVisible = sleepInfo.numberOfSleeps > 1
            remove2.isVisible = !layout2.isVisible
            layout2.isVisible = sleepInfo.numberOfSleeps > 2
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
        getSleepingTimesFromPicker(hourOfDay, minute)
    }

    private fun getSleepingTimesFromPicker(hourOfDay: Int, minute: Int) {
        when (tag) {
            1, 3, 5 -> {
                if (tag == 1) tag = 0
                if (tag == 3) tag = 1
                if (tag == 5) tag = 2
                prefBedHourList[tag] = hourOfDay
                prefBedMinuteList[tag] = minute
                when (tag) {
                    0 -> {
                        binding.bedText1.text =
                            getString(R.string.preferred_bed_times, hourOfDay, minute)
                        binding.submitButton.isEnabled = true
                    }
                    1 -> {
                        binding.bedText2.text =
                            getString(R.string.preferred_bed_times, hourOfDay, minute)
                        bedSubmitted = true
                        sleepTwoIsValid = bedSubmitted && wokeSubmitted
                        binding.submitButton.isEnabled = bedSubmitted && wokeSubmitted
                    }
                    2 -> {
                        binding.bedText3.text =
                            getString(R.string.preferred_bed_times, hourOfDay, minute)
                        bedSubmitted = true
                        binding.submitButton.isEnabled = bedSubmitted && wokeSubmitted
                    }

                }

            }
            2, 4, 6 -> {
                if (tag == 2) tag = 0
                if (tag == 4) tag = 1
                if (tag == 6) tag = 2

                prefWokeUpHourList[tag] = hourOfDay
                prefWokeUpMinutesList[tag] = minute
                when (tag) {
                    0 -> {
                        binding.wokeText1.text =
                            getString(R.string.preferred_woke_up_times, hourOfDay, minute)
                        binding.submitButton.isEnabled = true
                    }
                    1 -> {
                        binding.wokeText2.text =
                            getString(R.string.preferred_woke_up_times, hourOfDay, minute)
                        wokeSubmitted = true
                        sleepTwoIsValid = bedSubmitted && wokeSubmitted
                        binding.submitButton.isEnabled = bedSubmitted && wokeSubmitted
                    }
                    2 -> {
                        binding.wokeText3.text =
                            getString(R.string.preferred_woke_up_times, hourOfDay, minute)
                        wokeSubmitted = true
                        binding.submitButton.isEnabled = bedSubmitted && wokeSubmitted
                    }
                }
            }
        }
    }

    private fun getSleepTimes() {
        prefWokeUpHourList = sleepInfo.prefWokeUpHours
        prefWokeUpMinutesList = sleepInfo.prefWokeUpMinutes
        prefBedHourList = sleepInfo.prefBedTimeHours
        prefBedMinuteList = sleepInfo.prefBedTimeMinutes
    }

    private fun setViews() {
        binding.apply {
            bedText1.text =
                getString(R.string.preferred_bed_times, prefBedHourList[0], prefBedMinuteList[0])
            bedText2.text =
                getString(R.string.preferred_bed_times, prefBedHourList[1], prefBedMinuteList[1])
            bedText3.text =
                getString(R.string.preferred_bed_times, prefBedHourList[2], prefBedMinuteList[2])
            wokeText1.text = getString(
                R.string.preferred_woke_up_times,
                prefWokeUpHourList[0],
                prefWokeUpMinutesList[0]
            )
            wokeText2.text = getString(
                R.string.preferred_woke_up_times,
                prefWokeUpHourList[1],
                prefWokeUpMinutesList[1]
            )
            wokeText3.text = getString(
                R.string.preferred_woke_up_times,
                prefWokeUpHourList[2],
                prefWokeUpMinutesList[2]
            )
        }
    }

    private fun checkTimesBeforeSubmit() {
        val bedTimePastMidnight =
            prefBedHourList[0] < prefWokeUpHourList[0]
        var submitTimes = true
        val bed1 = prefBedHourList[0].toDouble() + (prefBedMinuteList[0].toDouble() / 60)
        val bed2 = prefBedHourList[1].toDouble() + (prefBedMinuteList[1].toDouble() / 60)
        val bed3 = prefBedHourList[2].toDouble() + (prefBedMinuteList[2].toDouble() / 60)
        val woke1 = prefWokeUpHourList[0].toDouble() + (prefWokeUpMinutesList[0] / 60).toDouble()
        val woke2 = prefWokeUpHourList[1].toDouble() + (prefWokeUpMinutesList[1] / 60).toDouble()
        val woke3 = prefWokeUpHourList[2].toDouble() + (prefWokeUpMinutesList[2] / 60).toDouble()

        if (bed1 > 5 && bed1 < 17) {
            Toast.makeText(requireContext(), "Bed time for you main sleep, cannot be in the morning or afternoon.", Toast.LENGTH_LONG).show()
            submitTimes = false
        }
        if (numberOfSleeps > 1) {
            when (bedTimePastMidnight) {
                true -> {
                    if (bed2 < bed1 || bed2 < woke1 || woke2 < woke1) {
                        Toast.makeText(
                            requireContext(),
                            "Day sleeps cannot be in between your main sleeps.",
                            Toast.LENGTH_SHORT
                        ).show()
                        submitTimes = false
                    }
                }
                false -> {
                    if (bed2 > bed1 || woke2 > bed1 || woke2<woke1|| bed2<woke1) {
                        Toast.makeText(
                            requireContext(),
                            "Day sleeps cannot be in between your main sleeps.",
                            Toast.LENGTH_SHORT
                        ).show()
                        submitTimes = false
                    }
                }
            }
            if (bed2>woke2) {
                Toast.makeText(requireContext(), "Bed time must be before woke up time.", Toast.LENGTH_SHORT).show()
                submitTimes = false
            }
        }
        if (numberOfSleeps > 2) {
            when (bedTimePastMidnight) {
                true -> {
                    if (bed3 < bed1 || bed3 < woke1 || woke3 < woke1) {
                        Toast.makeText(
                            requireContext(),
                            "Day sleeps cannot be in between your main sleeps.Or after midnight.",
                            Toast.LENGTH_LONG
                        ).show()
                        submitTimes = false
                    }
                }
                false -> {
                    if (bed3 > bed1 || woke3 > bed1 || woke3<woke1 || bed3<woke1) {
                        Toast.makeText(
                            requireContext(),
                            "Day sleeps cannot be in between your main sleeps. Or after midnight.",
                            Toast.LENGTH_LONG
                        ).show()
                        submitTimes = false
                    }
                }
            }
            if (bed3>woke3) {
                Toast.makeText(requireContext(), "Bed time must be before woke up time.", Toast.LENGTH_LONG).show()
                submitTimes = false
            }
        }
        if (submitTimes) submitNewSleepTimes()
    }

    private fun submitNewSleepTimes() {
        val newSleepInfo = SleepingInfo(
            prefWokeUpHourList,
            prefWokeUpMinutesList,
            prefBedHourList,
            prefBedMinuteList,
            numberOfSleeps,
            sleepInfo.isSubmitted,
            sleepInfo.totalPointsEarned,
            sleepInfo.currentDay,
            sleepInfo.currentYear,
            sleepInfo.numberOfSubmits
        )
        val bedTimePastMidnight =
            prefBedHourList[0] < prefWokeUpHourList[0]

        // Insert sleeping times into day planner....->
        if (newSleepInfo.numberOfSleeps > 0) {
            val plannerDataWokeUp = PlannerData(
                newSleepInfo.prefWokeUpHours[0],
                newSleepInfo.prefWokeUpMinutes[0],
                "",
                getString(R.string.woke_up_time_planner, ""),
                "Rise and shine!",
                true, false
            )

            val plannerDataBedTime = PlannerData(
                newSleepInfo.prefBedTimeHours[0],
                newSleepInfo.prefBedTimeMinutes[0],
                "",
                getString(R.string.bed_time_planner, ""),
                "Sweet dreams...",
                false,
                true,
                bedTimePastMidnight
            )
            viewModel_.insertPlannerDataToday(plannerDataWokeUp)
            viewModel_.insertPlannerDataToday(plannerDataBedTime)
            viewModel_.insertPlannerDataTomorrow(plannerDataBedTime)
            viewModel_.insertPlannerDataTomorrow(plannerDataWokeUp)
            when (numberOfSleeps) {
                1 -> {
                    deletePlannerData(1)
                    deletePlannerData(2)
                }
                2 -> {
                    deletePlannerData(2)
                }
            }
        }

        if (newSleepInfo.numberOfSleeps > 1) {
            val plannerDataWokeUp = getWokeSleepPlannerData(1, newSleepInfo)
            val plannerDataBedTime = getBedSleepPlannerData(1, newSleepInfo)
            viewModel_.insertPlannerDataToday(plannerDataWokeUp)
            viewModel_.insertPlannerDataToday(plannerDataBedTime)
            viewModel_.insertPlannerDataTomorrow(plannerDataBedTime)
            viewModel_.insertPlannerDataTomorrow(plannerDataWokeUp)
            when (numberOfSleeps) {
                2 -> deletePlannerData(2)
            }

        }

        if (newSleepInfo.numberOfSleeps > 2) {
            val plannerDataWokeUp = getWokeSleepPlannerData(2, newSleepInfo)
            val plannerDataBedTime = getBedSleepPlannerData(2, newSleepInfo)
            viewModel_.insertPlannerDataToday(plannerDataWokeUp)
            viewModel_.insertPlannerDataToday(plannerDataBedTime)
            viewModel_.insertPlannerDataTomorrow(plannerDataBedTime)
            viewModel_.insertPlannerDataTomorrow(plannerDataWokeUp)
        }

        if (!sleepInfo.isSubmitted) {
            val gson = Gson()
            val gsonString = gson.toJson(
                SleepingSavedState(newSleepInfo)
            )
            SharedPref(requireActivity()).saveString(Keys.SLEEP_SAVED_STATE, gsonString)
        }

        findNavController().navigate(
            ChangeSleepTimesDirections.actionChangeSleepTimesToSleepingMain(
                newSleepInfo
            )
        )
        viewModel_.insertSleepInfo(newSleepInfo)
    }

    inner class MyUndoListener(
        val view: View,
        private val hourBed: Int,
        private val minuteBed: Int,
        private val hourWoke: Int,
        private val minuteWoke: Int,
        private val index: Int,
        private val wenToBedText: TextView,
        private val wokeUpText: TextView
    ) : View.OnClickListener {

        override fun onClick(v: View) {
            view.isVisible = true
            prefBedHourList[index] = hourBed
            prefBedMinuteList[index] = minuteBed
            prefWokeUpHourList[index] = hourWoke
            prefWokeUpMinutesList[index] = minuteWoke

            wenToBedText.text = getString(
                R.string.preferred_bed_times,
                prefBedHourList[index],
                prefBedMinuteList[index]
            )

            wokeUpText.text = getString(
                R.string.preferred_woke_up_times,
                prefWokeUpHourList[index],
                prefWokeUpMinutesList[index]
            )
        }
    }
}