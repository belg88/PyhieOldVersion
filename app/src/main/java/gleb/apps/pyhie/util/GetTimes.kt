package gleb.apps.pyhie.util

import java.util.*

class GetTimes() {
    val calendar: Calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
}