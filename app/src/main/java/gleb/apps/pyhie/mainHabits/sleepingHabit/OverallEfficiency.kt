package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.firebase.FirebaseService


class OverallEfficiency : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var text: TextView
    private lateinit var averageTimeSlept: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_overall_efficiency, container, false)
        progressBar = view.findViewById(R.id.efficiency_bar)
        text = view.findViewById(R.id.level_text)
        averageTimeSlept = view.findViewById(R.id.average_time_slept)

        overallBalance()
        return view
    }

    private fun overallBalance() {
        var sleepDuration = 0.0
        var bedTime = 0.0
        var wokeTime = 0.0
        var quality = 0.0
        var averageSleepQuality = 0.0
        val habitData = SharedPref(requireActivity()).getString(Keys.MAIN_HABITS)

        lifecycleScope.launchWhenCreated {
            when (habitData) {
                Keys.SLEEP_DATA -> {
                    val list =
                        FirebaseService.getSleepingData(SharedPref(requireActivity()).getString(
                            Keys.EMAIL)!!)
                    if (list != null) {
                        for (x in list.indices) {
                            sleepDuration += list[x].timeSlept()
                            bedTime += list[x].bedTime()
                            wokeTime += list[x].wokeUpTime()
                            quality += list[x].getSleepQuality()
                            averageSleepQuality = (quality / list.size) * 100
                        }
                    }
                }
                Keys.EATING_DATA -> {
                    val list =
                        FirebaseService.getEatingData(SharedPref(requireActivity()).getString(
                            Keys.EMAIL)!!)
                    if (list != null) {
                        for (x in list.indices) {
                            quality += list[x].getQuality()
                            averageSleepQuality = (quality / list.size) * 100
                        }
                    }
                }
                Keys.CLEANING_DATA -> {
                    val list =
                        FirebaseService.getCleaningData(SharedPref(requireActivity()).getString(
                            Keys.EMAIL)!!)
                    if (list != null) {
                        for (x in list.indices) {
                            quality += list[x].quality
                            averageSleepQuality = quality / list.size
                        }
                    }
                }
            }

            if (averageSleepQuality >= 80) {
                averageTimeSlept.text = when (habitData) {
                   Keys.SLEEP_DATA -> getString(R.string.great_overall,"sleeping quality")
                   Keys.EATING_DATA -> getString(R.string.great_overall,"eating quality")
                   Keys.CLEANING_DATA -> getString(R.string.great_overall,"cleaning quality")
                    else -> ""
                }
            }
            if (averageSleepQuality >= 60 && averageSleepQuality < 80) {
                averageTimeSlept.text = when (habitData) {
                    Keys.SLEEP_DATA -> getString(R.string.not_bad_overall,"sleeping quality")
                    Keys.EATING_DATA -> getString(R.string.not_bad_overall,"eating quality")
                    Keys.CLEANING_DATA -> getString(R.string.not_bad_overall,"cleaning quality")
                    else -> ""
                }
            }
            if (averageSleepQuality >= 40 && averageSleepQuality < 60) {
                averageTimeSlept.text = when (habitData) {
                    Keys.SLEEP_DATA -> getString(R.string.room_for_overall,"sleeping quality")
                    Keys.EATING_DATA -> getString(R.string.room_for_overall,"eating quality")
                    Keys.CLEANING_DATA -> getString(R.string.room_for_overall,"cleaning quality")
                    else -> ""
                }
            }
            if (averageSleepQuality >= 20 && averageSleepQuality < 40 && averageSleepQuality > 0) {
                averageTimeSlept.text = when (habitData) {
                    Keys.SLEEP_DATA -> getString(R.string.bad_overall,"sleeping quality")
                    Keys.EATING_DATA -> getString(R.string.bad_overall,"eating quality")
                    Keys.CLEANING_DATA -> getString(R.string.bad_overall        ,"cleaning quality")
                    else -> ""
                }
            }
            if (averageSleepQuality == 0.0) {
                averageTimeSlept.text = ""
            }

            val animator = if (averageSleepQuality == 0.0) ObjectAnimator.ofInt(
                progressBar,
                "progress",
                1
            ) else ObjectAnimator.ofInt(
                progressBar,
                "progress",
                averageSleepQuality.toInt()
            )
            animator.duration = 1500
            animator.start()

            val anim = ValueAnimator()
            anim.setObjectValues(0, averageSleepQuality.toInt())
            anim.addUpdateListener {
                text.text = it.animatedValue.toString() + getString(R.string.percent)
            }
            anim.duration = 1500
            anim.start()

        }

    }
}

class ConvertToTime() {

    fun getMinutes(timeValue: Double): Int {
        val realMinutes: Double
        val minutes = timeValue.toInt() - timeValue
        realMinutes = if (minutes <= 0) {
            minutes * 60 * -1
        } else {
            (1 - minutes) * 60
        }
        return realMinutes.toInt()
    }

    fun getHour(timeValue: Double): Int {
        val time = timeValue - timeValue.toInt()
        return if (time < 0) {
            timeValue.toInt() - 1
        } else {
            timeValue.toInt()
        }
    }
}
