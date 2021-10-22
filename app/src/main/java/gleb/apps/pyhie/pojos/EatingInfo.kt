package gleb.apps.pyhie.pojos

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EatingInfo(
    // Preferred times for meals:
    val mealHour: MutableList<Int> = mutableListOf(0,0,0,0,0),
    val mealMinute: MutableList<Int> = mutableListOf(0,0,0,0,0),
    var numberOfMeals: Int = 1,
    var submitted: Boolean = false,
    var currentDay: Int = 0,
    var currentYear: Int = 0,
    var numberOfSubmits: Int = 0,
    var totalPoints: Double = 0.0

):Parcelable {
    @IgnoredOnParcel
    private val stringArray = arrayOf("", "", "","","")

    fun totalEatingTime(): Double{
        val time = mutableListOf(0.0,0.0,0.0,0.0,0.0)
        var timeDouble = 0.0
        for (x in 0 until numberOfMeals) {
            time[x] = mealHour[x].toDouble() + mealMinute[x].toDouble() / 60
            timeDouble = time.sum()

        }
        return timeDouble
    }

    private fun eatTimeStringArray(): Array<String> {
        for (x in 0 until numberOfMeals) {
            stringArray[x] = "${String.format("%02d", mealHour[x])}:${String.format(
                "%02d",
                mealMinute[x]
            )}"
        }
        return stringArray
    }

    fun eatingTimeString(): String {
        var string = ""
        val stringArray = arrayOf("", "", "","","")
        for (x in 0 until numberOfMeals) {
            stringArray[x] = "Your goal is to have meal number ${x+1} at: ${eatTimeStringArray()[x]}"
        }
        when (numberOfMeals) {
            1 -> string = stringArray[0]
            2 -> string = "${stringArray[0]}\n${stringArray[1]}"
            3 -> string = "${stringArray[0]}\n${stringArray[1]}\n${stringArray[2]}"
            4 -> string = "${stringArray[0]}\n${stringArray[1]}\n${stringArray[2]}\n${stringArray[3]}"
            5 -> string = "${stringArray[0]}\n${stringArray[1]}\n${stringArray[2]}\n${stringArray[3]}\n${stringArray[4]}"
        }
        return string
    }
}