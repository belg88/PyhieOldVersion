package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SleepingViewModelFactory(private val email: String, private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepingViewModel::class.java)) {
            return SleepingViewModel(
                email,
                app
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}