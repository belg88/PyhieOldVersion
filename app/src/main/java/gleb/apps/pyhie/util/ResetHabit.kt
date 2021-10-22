package gleb.apps.pyhie.util

import android.util.Log
import java.util.*

class ResetHabit(private val savedDay:Int, private val savedYear:Int) {
    fun isNextDay(): Boolean {
        val calendar = Calendar.getInstance()
        val currentDay: Int = if (savedYear == calendar.get(Calendar.YEAR)) {
            calendar.get(Calendar.DAY_OF_YEAR)
        } else {
            when (savedYear) {
                2024 -> Calendar.DAY_OF_YEAR + 366
                2028 -> Calendar.DAY_OF_YEAR + 366
                2032 -> Calendar.DAY_OF_YEAR + 366
                2036 -> Calendar.DAY_OF_YEAR + 366
                2040 -> Calendar.DAY_OF_YEAR + 366
                else -> Calendar.DAY_OF_YEAR + 365
            }
        }
        return currentDay != savedDay
    }
}