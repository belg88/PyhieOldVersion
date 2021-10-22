package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gleb.apps.pyhie.BR
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.R
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingData
import gleb.apps.pyhie.pojos.SleepingInfo
import gleb.apps.pyhie.pojos.UserPoints
import gleb.apps.pyhie.recyclerview.RecyclerItem
import kotlinx.coroutines.launch

class
SleepingViewModel(private val email: String, private val app: Application) : ViewModel() {
    val sleepData = MutableLiveData<List<RecyclerItem>>()
    private val userPoints = MutableLiveData<UserPoints>()
    val getUserPoints: LiveData<UserPoints> = userPoints



    init {
        viewModelScope.launch {
            sleepData.value = FirebaseService.getSleepingData(email)?.map {
                it.toRecyclerItem()
            }
            userPoints.value = FirebaseService.getUserPoints(email)
        }

    }

    fun insertSleepInfo(sleepingInfo: SleepingInfo) =
        FirebaseService.insertSleepingPref(sleepingInfo, email)

    fun insertSleepData(sleepingData: SleepingData) =
        FirebaseService.insertSleepingData(sleepingData, email)

    fun insertUserPoints(userPoints: UserPoints) = FirebaseService.insertUserPoints(userPoints, email)

    fun insertPlannerDataToday(plannerData: PlannerData) = viewModelScope.launch {
        FirebaseService.insertPlannerDataToday(plannerData, email)
    }

    fun insertPlannerDataTomorrow(plannerData: PlannerData) = viewModelScope.launch {
        FirebaseService.insertPlannerDataTomorrow(plannerData, email)
    }


    private fun SleepingData.toRecyclerItem() =
        RecyclerItem(this, R.layout.sleep_item, BR.sleepingData)
}