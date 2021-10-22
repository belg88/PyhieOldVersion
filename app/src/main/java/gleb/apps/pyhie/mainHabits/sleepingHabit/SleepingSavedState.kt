package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.graphics.Color
import android.opengl.Visibility
import android.view.View
import android.widget.Button
import gleb.apps.pyhie.pojos.SleepingInfo

data class SleepingSavedState(
    var sleepingInfo: SleepingInfo = SleepingInfo(),
    var bedTimeHoursList: MutableList<Int> = mutableListOf(0,0,0),
    var bedTimeMinutesList: MutableList<Int> = mutableListOf(0,0,0),
    var wokeUpHoursList: MutableList<Int> = mutableListOf(0,0,0),
    var wokeUpMinutesList: MutableList<Int> = mutableListOf(0,0,0),
    var bedTimeText1: String = "What time did you go to bed last night?",
    var bedTimeText2: String = "What time did you go for mid day nap?",
    var bedTimeText3: String = "What time did you go for mid day nap?",
    var prefBedTimeText1: String = "(Preferred time is ${String.format("%02d",sleepingInfo.prefBedTimeHours[0])}:${String.format("%02d",sleepingInfo.prefBedTimeMinutes[0])})",
    var prefBedTimeText2: String = "(Preferred time is ${String.format("%02d",sleepingInfo.prefBedTimeHours[1])}:${String.format("%02d",sleepingInfo.prefBedTimeMinutes[1])})",
    var prefBedTimeText3: String = "(Preferred time is ${String.format("%02d",sleepingInfo.prefBedTimeHours[2])}:${String.format("%02d",sleepingInfo.prefBedTimeMinutes[2])})",
    var wokeUpTimeText1: String = "What time did you woke up?",
    var wokeUpTimeText2: String = "What time did you woke up?",
    var wokeUpTimeText3: String = "What time did you woke up?",
    var prefWokeUpTimeText1: String = "(Preferred time is ${String.format("%02d",sleepingInfo.prefWokeUpHours[0])}:${String.format("%02d",sleepingInfo.prefWokeUpMinutes[0])})",
    var prefWokeUpTimeText2: String = "(Preferred time is ${String.format("%02d",sleepingInfo.prefWokeUpHours[1])}:${String.format("%02d",sleepingInfo.prefWokeUpMinutes[1])})",
    var prefWokeUpTimeText3: String = "(Preferred time is ${String.format("%02d",sleepingInfo.prefWokeUpHours[2])}:${String.format("%02d",sleepingInfo.prefWokeUpMinutes[2])})",
    var currentRating: String = "BedTime1",
    var bedTextColor1: Int = Color.GRAY,
    var bedTextColor2: Int = Color.GRAY,
    var bedTextColor3: Int = Color.GRAY,
    var wokeTextColor1: Int = Color.GRAY,
    var wokeTextColor2: Int = Color.GRAY,
    var wokeTextColor3: Int = Color.GRAY,
    var prefBedVisibility1: Int = View.VISIBLE,
    var prefBedVisibility2: Int = View.VISIBLE,
    var prefBedVisibility3: Int = View.VISIBLE,
    var prefWokeUpVisibility1: Int = View.VISIBLE,
    var prefWokeUpVisibility2: Int = View.VISIBLE,
    var prefWokeUpVisibility3: Int = View.VISIBLE,
    var wokeUpLayoutVisibility1: Int = View.GONE,
    var wokeUpLayoutVisibility2: Int = View.GONE,
    var wokeUpLayoutVisibility3: Int = View.GONE,
    var bedLayoutVisibility2: Int = View.GONE,
    var bedLayoutVisibility3: Int = View.GONE,
    var selectButtonVisibility: Int = View.VISIBLE,
    var ratingsLayoutVisibility: Int = View.GONE,
    var submitVisibility: Int = View.GONE,
    var undoVisibility: Int = View.GONE
)