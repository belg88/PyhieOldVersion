package gleb.apps.pyhie.pojos

import android.util.Log
import gleb.apps.pyhie.R
import gleb.apps.pyhie.util.GetDates
import gleb.apps.pyhie.util.UserConstants
import java.util.*

data class EatingData(
    val mealHour: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0),
    val mealMinute: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0),
    val mealHealthiness: MutableList<String> = mutableListOf("", "", "", ""),
    val eatingInfo: EatingInfo = EatingInfo()
) {
    val timeStamp = Date().time
    val weekdayString = GetDates().weekdayString(timeStamp)
    val dateString = GetDates().dateString(timeStamp)
    val weekNumber = GetDates().weekNumber
    private var string = ""
    private val stringArray = arrayOf("", "", "", "", "")
    private val percentList = mutableListOf(0.0, 0.0, 0.0, 0.0)

    private fun healthinessPercent(): Double {
        for (x in 0 until eatingInfo.numberOfMeals) {
            when (mealHealthiness[x]) {
                UserConstants.HEALTHY -> percentList[x] = 1.0
                UserConstants.PART_HEALTHY -> percentList[x] = 0.8
                UserConstants.PART_UNHEALTHY -> percentList[x] = 0.5
                UserConstants.UNHEALTHY -> percentList[x] = 0.2
            }
        }
        return percentList.sum() / eatingInfo.numberOfMeals
    }

    private fun healthinessPoints(): Double {
        for (x in 0 until eatingInfo.numberOfMeals) {
            when (mealHealthiness[x]) {
                UserConstants.HEALTHY -> percentList[x] = UserConstants.MAX_POINTS
                UserConstants.PART_HEALTHY -> percentList[x] = UserConstants.MEDIUM_POINTS
                UserConstants.PART_UNHEALTHY -> percentList[x] = UserConstants.SMALL_POINTS
                UserConstants.UNHEALTHY -> percentList[x] = UserConstants.MIN_POINTS
            }
        }
        return percentList.sum() / eatingInfo.numberOfMeals
    }

    private fun totalEatingTime(): Double {
        val time = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0)
        var timeDouble = 0.0

        for (x in 0 until eatingInfo.numberOfMeals) {
            time[x] = mealHour[x].toDouble() + mealMinute[x].toDouble() / 60
            timeDouble = time.sum()
        }
        return timeDouble
    }

    private fun eatTimeStringArray(): Array<String> {
        for (x in 0 until eatingInfo.numberOfMeals) {
            stringArray[x] = "${String.format("%02d", mealHour[x])}:${String.format(
                "%02d",
                mealMinute[x]
            )}"
        }
        return stringArray
    }

    fun eatingTimeString(): String {
        string = ""
        val stringArray = arrayOf("", "", "", "", "")
        for (x in 0 until eatingInfo.numberOfMeals) {
            stringArray[x] = "Meal ${x+1} at: ${eatTimeStringArray()[x]}, and it was ${mealHealthiness[x]}."
        }
        when (eatingInfo.numberOfMeals) {
            1 -> string = stringArray[0]
            2 -> string = "${stringArray[0]}\n${stringArray[1]}"
            3 -> string = "${stringArray[0]}\n${stringArray[1]}\n${stringArray[2]}"
            4 -> string =
                "${stringArray[0]}\n${stringArray[1]}\n${stringArray[2]}\n${stringArray[3]}"
            5 -> string =
                "${stringArray[0]}\n${stringArray[1]}\n${stringArray[2]}\n${stringArray[3]}\n${stringArray[4]}"
        }
        return string
    }

    fun getPoints(): Double {
        val points: Double
        var timeDifference = totalEatingTime() - eatingInfo.totalEatingTime()

        if (timeDifference < 0) {
            timeDifference *= -1
        }

        if (timeDifference <= 0.5) points = UserConstants.MAX_POINTS
        else if (timeDifference <= 1) points = UserConstants.MEDIUM_POINTS
        else if (timeDifference <= 1.5) points = UserConstants.SMALL_POINTS
        else if (timeDifference <= 2) points = UserConstants.MIN_POINTS
        else points = 0.0
        return (points + healthinessPoints()) / 2
    }

    fun getQuality(): Double {
        var difference = totalEatingTime() - eatingInfo.totalEatingTime()
        var quality = 0.0
        if (difference < 0) {
            difference *= -1
        }

        if (difference < 0.2) return 1.0
        if (difference < 0.4 && difference >= 0.2) quality = 0.9
        if (difference < 0.6 && difference >= 0.4) quality = 0.8
        if (difference < 0.8 && difference >= 0.6) quality = 0.7
        if (difference < 1 && difference >= 0.8) quality = 0.6
        if (difference < 1.4 && difference >= 1.0) quality = 0.5
        if (difference < 1.8 && difference >= 1.4) quality = 0.4
        if (difference < 2.2 && difference >= 1.8) quality = 0.35
        if (difference < 2.6 && difference >= 2.2) quality = 0.20
        if (difference < 3 && difference >= 2.6) quality = 0.15
        if (difference > 3) quality = 0.1
        return (quality + healthinessPercent()) / 2
    }

    fun getQualityImage(): Int {
        return  when (getQuality()) {
            in 0.8..1.0 -> R.drawable.very_happy_24
            in 0.6..0.8 -> R.drawable.happy_24
            in 0.4..0.6 -> R.drawable.normal_24
            else -> R.drawable.unhappy_24
        }
    }

    val pointsString = "Points earned: ${String.format("%.1f", getPoints()).toDouble()}"

}