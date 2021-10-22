package gleb.apps.pyhie.mainHabits.sleepingHabit.planningHabit

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlannerInfo(
    var submitted: Boolean = false,
    var currentDay: Int = 0,
    var currentYear: Int = 0,
    var totalPoints: Double = 0.0,
    var numberOfSubmits: Int = 0
) : Parcelable