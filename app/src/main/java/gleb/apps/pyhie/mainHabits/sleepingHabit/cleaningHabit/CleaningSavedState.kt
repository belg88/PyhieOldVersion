package gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.view.View
import gleb.apps.pyhie.R

data class CleaningSavedState(
    var bedroomText: String = "How would you describe state of your living room and bedroom?",
    var kitchenText: String = "How would you describe state of your kitchen?",
    var bedroomRating:String = "",
    var kitchenRating:String = "",
    var dishesText: String = "Look around you living area, does it look organised and everything is where it suppose to be?",
    var clothesText: String = "Do you have any laundry that needs to be done?",
    var dishesDone: Boolean = false,
    var clothesDone: Boolean = false,
    var submitVisibility: Int = View.GONE,
    var kitchenVisibility: Int = View.GONE,
    var dishesVisibility: Int = View.GONE,
    var clothesVisibility: Int = View.GONE,
    var ratingVisibility: Int = View.VISIBLE,
    var yesNoVisibility: Int = View.GONE,
    var currentRating: String = "Bedroom",
    var bedroomColor: Int = Color.GRAY,
    var kitchenColor: Int = Color.GRAY,
    var dishesColor: Int = Color.GRAY,
    var clothesColor: Int = Color.GRAY,
    var undoVisibility: Int = View.GONE,
    var submitTextVisibility: Int = View.GONE
) {
}