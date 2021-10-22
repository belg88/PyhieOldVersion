package gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gleb.apps.pyhie.mainHabits.sleepingHabit.SleepingViewModel

class EatingViewModelFactory(private val email: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EatingViewModel::class.java)) {
            return EatingViewModel(
                email
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}