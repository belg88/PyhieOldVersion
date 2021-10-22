package gleb.apps.pyhie.mainHabits.sleepingHabit.planningHabit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gleb.apps.pyhie.mainHabits.sleepingHabit.SleepingViewModel

class PlanningViewModelFactory(private val email: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanningHabitViewModel::class.java)) {
            return PlanningHabitViewModel(
                email
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}