package gleb.apps.pyhie.pojos

import android.os.Parcelable
import gleb.apps.pyhie.util.UserConstants
import gleb.apps.pyhie.util.ResetHabit
import gleb.apps.pyhie.util.GetDates
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*
@Parcelize
data class SleepingInfo(
    //using lists in case there are more than one sleep cycle chosen by user
    val prefWokeUpHours: MutableList<Int> = mutableListOf(0,0,0),
    val prefWokeUpMinutes: MutableList<Int> = mutableListOf(0,0,0),
    val prefBedTimeHours: MutableList<Int> = mutableListOf(0,0,0),
    val prefBedTimeMinutes: MutableList<Int> = mutableListOf(0,0,0),
    val numberOfSleeps: Int = 1,
    var isSubmitted: Boolean = false,
    var totalPointsEarned: Double = 0.0,
    var currentDay: Int = 0,
    var currentYear: Int = 0,
    var numberOfSubmits: Int = 0

):Parcelable {

    @IgnoredOnParcel
    val maxPoints = numberOfSubmits * UserConstants.MAX_POINTS
    @IgnoredOnParcel
    private val timeStamp = Date().time

    fun wokeUpTime(): Double {
        val time = mutableListOf(0.0,0.0,0.0)
        var timeDouble = 0.0
        for (x in prefWokeUpHours.indices) {
            time[x] = prefWokeUpHours[x].toDouble() + prefWokeUpMinutes[x].toDouble() / 60
            timeDouble = time.sum()

        }
        return timeDouble
    }

    fun bedTime(): Double {
        val time = mutableListOf(0.0,0.0,0.0)
        var timeDouble = 0.0
        for (x in prefBedTimeHours.indices) {
            time[x] = prefBedTimeHours[x].toDouble() + prefBedTimeMinutes[x].toDouble() / 60
            timeDouble = time.sum()
        }
        return timeDouble
    }

    fun timeSlept(): Double {
        var time = 0.0
        val timeArray = arrayOf(0.0, 0.0, 0.0)
        val bedTime = arrayOf(0.0,0.0,0.0)
        val wokeUpTime= arrayOf(0.0,0.0,0.0)

        for (x in 0 until numberOfSleeps) {
            bedTime[x] = prefBedTimeHours[x].toDouble() + prefBedTimeMinutes[x].toDouble()/60
            wokeUpTime[x] = prefWokeUpHours[x].toDouble() + prefWokeUpMinutes[x].toDouble()/60

            timeArray[x] = if (bedTime[x] > wokeUpTime[x]) {
                wokeUpTime[x] + 24 - bedTime[x]
            } else {
                wokeUpTime[x] - bedTime[x]
            }
            time = timeArray.sum()

            }
        return time
    }


}