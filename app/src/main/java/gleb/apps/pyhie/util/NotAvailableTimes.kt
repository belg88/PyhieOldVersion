package gleb.apps.pyhie.util

import android.app.Activity
import android.widget.Toast
import gleb.apps.pyhie.pojos.SleepingInfo
import kotlin.math.min

class NotAvailableTimes(hours: Int, minutes: Int) {
    val time =  (hours + minutes/60).toDouble()
    fun notCollidingWithSleep(
        sleepingInfo: SleepingInfo,
        activity: Activity
    ): Boolean {
        var bool = true
        val wokeUpTime = (sleepingInfo.prefWokeUpHours[0] + sleepingInfo.prefWokeUpMinutes[0]/60).toDouble()
        if (time <= wokeUpTime) {
            bool = false
            Toast.makeText(activity, "This time is not available.", Toast.LENGTH_LONG).show()
        }
        return bool
    }
    fun wokeUpIsAfterBed (wokeHours: Int, wokeMinutes: Int, activity: Activity):Boolean {
        var bool = true
        val wokeUpTime = (wokeHours + wokeMinutes/60).toDouble()
        if (wokeUpTime<=time) {
            bool = false
            Toast.makeText(activity, "Woke up time needs to be after bed time.", Toast.LENGTH_LONG)
                .show()
        }
        return bool
    }
}