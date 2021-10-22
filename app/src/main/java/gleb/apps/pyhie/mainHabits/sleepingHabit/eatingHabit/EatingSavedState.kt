package gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit

import android.graphics.Color
import android.opengl.Visibility
import android.view.View
import gleb.apps.pyhie.pojos.EatingInfo

data class EatingSavedState(
    var eatingInfo: EatingInfo = EatingInfo(),
    var meal1Text:String = "What time did you have your first meal?",
    var meal2Text:String = "What time did you have your second meal?",
    var meal3Text:String = "What time did you have your third meal?",
    var meal4Text:String = "What time did you have your fourth meal?",
    var meal5Text:String = "What time did you have your fifth meal?",
    var meal1PrefText:String = "(Preferred time is ${String.format("%02d",eatingInfo.mealHour[0])}:${String.format("%02d",eatingInfo.mealMinute[0])})",
    var meal2PrefText:String = "(Preferred time is ${String.format("%02d",eatingInfo.mealHour[1])}:${String.format("%02d",eatingInfo.mealMinute[1])})",
    var meal3PrefText:String = "(Preferred time is ${String.format("%02d",eatingInfo.mealHour[2])}:${String.format("%02d",eatingInfo.mealMinute[2])})",
    var meal4PrefText:String = "(Preferred time is ${String.format("%02d",eatingInfo.mealHour[3])}:${String.format("%02d",eatingInfo.mealMinute[3])})",
    var meal5PrefText:String = "(Preferred time is ${String.format("%02d",eatingInfo.mealHour[4])}:${String.format("%02d",eatingInfo.mealMinute[4])})",
    var mealColor1:Int = Color.GRAY,
    var mealColor2:Int = Color.GRAY,
    var mealColor3:Int = Color.GRAY,
    var mealColor4:Int = Color.GRAY,
    var mealColor5:Int = Color.GRAY,
    var mealVisibility2: Int = View.GONE,
    var mealVisibility3: Int = View.GONE,
    var mealVisibility4: Int = View.GONE,
    var mealVisibility5: Int = View.GONE,
    var hourList:MutableList<Int> = mutableListOf(0,0,0,0,0),
    var minuteList:MutableList<Int> = mutableListOf(0,0,0,0,0),
    val healthinessList: MutableList<String> = mutableListOf("","","","",""),
    var submitVisibility: Int = View.GONE,
    var healthRatingVisibility: Int = View.GONE,
    var howMuchYouAteVisibility: Int = View.GONE,
    var eatTimeButtonVisibility: Int = View.VISIBLE,
    var ratingsLayoutVisibility: Int = View.VISIBLE,
    var currentRating:String = "Meal1",
    var currentMealTag:Int = 0,
    var prefTextVisibility1:Int = View.VISIBLE,
    var prefTextVisibility2:Int = View.VISIBLE,
    var prefTextVisibility3:Int = View.VISIBLE,
    var prefTextVisibility4:Int = View.VISIBLE,
    var prefTextVisibility5:Int = View.VISIBLE,
    var undoVisibility: Int = View.GONE
)