package gleb.apps.pyhie.pojos

import gleb.apps.pyhie.R
import gleb.apps.pyhie.mainHabits.sleepingHabit.ConvertToTime
import gleb.apps.pyhie.util.UserConstants
import gleb.apps.pyhie.util.GetDates
import java.util.*

data class SleepingData(
    val wokeUpHours: MutableList<Int> = mutableListOf(0),
    val wokeUpMinutes: MutableList<Int> = mutableListOf(0),
    val bedTimeHour: MutableList<Int> = mutableListOf(0),
    val bedTimeMinutes: MutableList<Int> = mutableListOf(0),
    val sleepingInfo: SleepingInfo = SleepingInfo()
) {

    val timeStamp = Date().time

    private val stringArray = arrayOf("", "", "")

    fun wokeUpTime(): Double {
        val time = mutableListOf(0.0, 0.0, 0.0)
        var timeDouble = 0.0
        for (x in wokeUpHours.indices) {
            time[x] = wokeUpHours[x].toDouble() + wokeUpMinutes[x].toDouble() / 60
            timeDouble = time.sum()

        }
        return timeDouble
    }

    fun bedTime(): Double {
        val time = mutableListOf(0.0, 0.0, 0.0)
        var timeDouble = 0.0
        for (x in bedTimeHour.indices) {
            time[x] = bedTimeHour[x].toDouble() + bedTimeMinutes[x].toDouble() / 60
            timeDouble = time.sum()

        }
        return timeDouble
    }


    private fun bedTimeStringArray(): Array<String> {
        for (x in bedTimeHour.indices) {
            stringArray[x] = "${String.format("%02d", bedTimeHour[x])}:${String.format(
                "%02d",
                bedTimeMinutes[x]
            )}"
        }
        return stringArray
    }

    private fun wokeUpStringArray(): Array<String> {
        for (x in wokeUpHours.indices) {
            stringArray[x] = "${String.format("%02d", wokeUpHours[x])}:${String.format(
                "%02d",
                wokeUpMinutes[x]
            )}"
        }
        return stringArray
    }


    fun timeSlept(): Double {
        var time = 0.0
        val timeArray = arrayOf(0.0, 0.0, 0.0)
        val bedTime = arrayOf(0.0, 0.0, 0.0)
        val wokeUpTime = arrayOf(0.0, 0.0, 0.0)

        for (x in 0 until sleepingInfo.numberOfSleeps) {
            bedTime[x] = bedTimeHour[x].toDouble() + bedTimeMinutes[x].toDouble() / 60
            wokeUpTime[x] = wokeUpHours[x].toDouble() + wokeUpMinutes[x].toDouble() / 60

            //adjust by adding 24 to wake up time, if went to bed is before 00:00
            timeArray[x] = if (bedTime[x] > wokeUpTime[x]) {
                wokeUpTime[x] + 24 - bedTime[x]
            } else {
                wokeUpTime[x] - bedTime[x]
            }
            time = timeArray.sum()

        }
        return time
    }

    val weekdayString = GetDates().weekdayString(timeStamp)
    val dateString = GetDates().dateString(timeStamp)
    val weekNumber = GetDates().weekNumber
    private var string = ""

    fun timeSleptString(): String {
        val stringArray = arrayOf("", "", "")
        for (x in 0 until sleepingInfo.numberOfSleeps) {
            stringArray[x] = "Went to bed at ${bedTimeStringArray()[x]}" +
                    " and woke up at ${wokeUpStringArray()[x]}"
        }
        when (sleepingInfo.numberOfSleeps) {
            1 -> string = stringArray[0]
            2 -> string = "${stringArray[0]}\n${stringArray[1]}"
            3 -> string = "${stringArray[0]}\n${stringArray[1]}\n${stringArray[2]}"
        }
        return string
    }

    fun getPoints(): Double {
        var wokeUpTimeDiff = wokeUpTime() - sleepingInfo.wokeUpTime()

        if (wokeUpTimeDiff < 0) {
            wokeUpTimeDiff *= -1
        }

        if (wokeUpTimeDiff > 4) {   // if difference is more than 4 it is likely that one of the time is past 00:00,
            // therefore need to add 24. If time is not pas 00:00 it will not affect points or quality anyway.

            val adjWokeTime = if (wokeUpTime() > sleepingInfo.wokeUpTime()) bedTime() else wokeUpTime() + 24
            val adjPrefWokeTime =
                if (sleepingInfo.wokeUpTime() > wokeUpTime()) sleepingInfo.wokeUpTime() else sleepingInfo.wokeUpTime() + 24
            wokeUpTimeDiff = adjPrefWokeTime - adjWokeTime
            if (wokeUpTimeDiff < 0) wokeUpTimeDiff *= -1
        }

        var bedTimeDiff = bedTime() - sleepingInfo.bedTime()
        if (bedTimeDiff < 0) bedTimeDiff *= -1

        if (bedTimeDiff > 4) {
            val adjBedTime = if (bedTime() > sleepingInfo.bedTime()) bedTime() else bedTime() + 24
            val adjPrefBedTime =
                if (sleepingInfo.bedTime() > bedTime()) sleepingInfo.bedTime() else sleepingInfo.bedTime() + 24
            bedTimeDiff = adjBedTime - adjPrefBedTime
            if (bedTimeDiff < 0) bedTimeDiff *= -1
        }
        val totalDiff = wokeUpTimeDiff + bedTimeDiff

        return if (totalDiff <= 0.5) UserConstants.MAX_POINTS
        else if (totalDiff <= 1) UserConstants.MEDIUM_POINTS
        else if (totalDiff <= 1.5) UserConstants.SMALL_POINTS
        else if (totalDiff <= 2) UserConstants.MIN_POINTS
        else 0.0
    }

    fun getSleepQuality(): Double {

        var quality = 0.0
        var wokeUpTimeDiff = wokeUpTime() - sleepingInfo.wokeUpTime()

        if (wokeUpTimeDiff < 0) {
            wokeUpTimeDiff *= -1
        }

        if (wokeUpTimeDiff > 4) {   // if difference is more than 4 it is likely that one of the time is past 00:00,
            // therefore need to add 24. If time is not pas 00:00 it will not affect points or quality anyway.

            val adjWokeTime = if (wokeUpTime() > sleepingInfo.wokeUpTime()) bedTime() else wokeUpTime() + 24
            val adjPrefWokeTime =
                if (sleepingInfo.wokeUpTime() > wokeUpTime()) sleepingInfo.wokeUpTime() else sleepingInfo.wokeUpTime() + 24
            wokeUpTimeDiff = adjPrefWokeTime - adjWokeTime
            if (wokeUpTimeDiff < 0) wokeUpTimeDiff *= -1
        }

        var bedTimeDiff = bedTime() - sleepingInfo.bedTime()
        if (bedTimeDiff < 0) bedTimeDiff *= -1

        if (bedTimeDiff > 4) {
            val adjBedTime = if (bedTime() > sleepingInfo.bedTime()) bedTime() else bedTime() + 24
            val adjPrefBedTime =
                if (sleepingInfo.bedTime() > bedTime()) sleepingInfo.bedTime() else sleepingInfo.bedTime() + 24
            bedTimeDiff = adjBedTime - adjPrefBedTime
            if (bedTimeDiff < 0) bedTimeDiff *= -1
        }

        val difference = wokeUpTimeDiff + bedTimeDiff

        if (difference < 0.2) return 1.0
        if (difference < 0.4 && difference >= 0.2) quality = 0.9
        if (difference < 0.6 && difference >= 0.4) quality = 0.8
        if (difference < 0.8 && difference >= 0.6) quality = 0.7
        if (difference < 1 && difference >= 0.8) quality = 0.6
        if (difference < 1.4 && difference >= 1.0) quality = 0.5
        if (difference < 1.8 && difference >= 1.4) quality = 0.40
        if (difference < 2.2 && difference >= 1.8) quality = 0.35
        if (difference < 2.6 && difference >= 2.2) quality = 0.2
        if (difference < 3 && difference >= 2.6) quality = 0.15
        if (difference > 3) quality = 0.1

        return quality
    }

        fun getQualityImage(): Int {
          return  when (getSleepQuality()) {
               in 0.8..1.0 -> R.drawable.very_happy_24
               in 0.6..0.8 -> R.drawable.happy_24
               in 0.4..0.6 -> R.drawable.normal_24
               else -> R.drawable.unhappy_24
            }
        }

    val durationString =
        "Time slept: ${ConvertToTime().getHour(timeSlept())} hours and ${ConvertToTime().getMinutes(
            timeSlept()
        )} minutes"

    val pointsString = "Points earned: ${getPoints()}"


}