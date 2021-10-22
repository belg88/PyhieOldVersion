package gleb.apps.pyhie.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class GetDates {

    private val cal = Calendar.getInstance()

    fun weekdayString(timeInMillis: Long): String =
        SimpleDateFormat("EEEE", Locale.getDefault()).format(timeInMillis)

    val weekNumber = cal.get(Calendar.WEEK_OF_YEAR)

    fun dateString(timeInMillis: Long): String =
        SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(timeInMillis)

    fun getDay(timeInMillis: Long): Int {
        cal.timeInMillis = timeInMillis
        return cal.get(Calendar.DAY_OF_YEAR)
    }

    fun getYear(timeInMillis: Long): Int {
        cal.timeInMillis = timeInMillis
        return cal.get(Calendar.YEAR)
    }


    val today = cal.get(Calendar.DAY_OF_YEAR)
    val year = cal.get(Calendar.YEAR)

    val todayDate = SimpleDateFormat("d MMMM", Locale.getDefault()).format(cal.time)

    @RequiresApi(Build.VERSION_CODES.O)
    fun tomorrowDate(): String {
        val tomorrow = LocalDate.now().plus(1, ChronoUnit.DAYS)
        return tomorrow.format(DateTimeFormatter.ofPattern("d MMMM"))
    }


}