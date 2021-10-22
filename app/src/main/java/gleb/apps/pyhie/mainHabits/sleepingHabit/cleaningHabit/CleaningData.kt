package gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit

import gleb.apps.pyhie.pojos.UserPoints
import gleb.apps.pyhie.util.GetDates
import gleb.apps.pyhie.util.UserConstants
import java.util.*

data class CleaningData(
    val stateOfBedroom: String = "",
    val stateOfKitchen: String = "",
    val anyDishes: Boolean = false,
    val anyClothes: Boolean = false
) {
    val timeStamp = Date().time
    val dayOfTheWeek = GetDates().weekdayString(timeStamp)
    val weekNumber = GetDates().weekNumber
    fun points(): Double {
        var bedroomPoints = 0.0
        var kitchenPoints = 0.0
        when (stateOfBedroom) {
            UserConstants.CLEAN -> bedroomPoints = UserConstants.MAX_POINTS / 2
            UserConstants.PART_CLEAN -> bedroomPoints = UserConstants.MEDIUM_POINTS / 2
            UserConstants.PART_UNCLEAN -> bedroomPoints = UserConstants.SMALL_POINTS / 2
            UserConstants.UNCLEAN -> bedroomPoints = UserConstants.MIN_POINTS / 2
        }
        when (stateOfKitchen) {
            UserConstants.CLEAN -> kitchenPoints = UserConstants.MAX_POINTS / 2
            UserConstants.PART_CLEAN -> kitchenPoints = UserConstants.MEDIUM_POINTS / 2
            UserConstants.PART_UNCLEAN -> kitchenPoints = UserConstants.SMALL_POINTS / 2
            UserConstants.UNCLEAN -> kitchenPoints = UserConstants.MIN_POINTS / 2
        }
        var totalPoints = bedroomPoints + kitchenPoints
        if (anyClothes) totalPoints /= 1.5
        if (anyDishes) totalPoints /= 1.5

        return totalPoints
    }

    val quality = points() / 10 * 100
}