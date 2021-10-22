package gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gleb.apps.pyhie.BR
import gleb.apps.pyhie.R
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.*
import gleb.apps.pyhie.recyclerview.RecyclerItem
import kotlinx.coroutines.launch

class
EatingViewModel(private val email: String) : ViewModel() {
    val eatingData = MutableLiveData<List<RecyclerItem>>()
    private val lastWeekData = MutableLiveData<List<EatingData>>()
    val getLastWeekData: LiveData<List<EatingData>> = lastWeekData
    private val thisWeekData = MutableLiveData<List<EatingData>>()
    val getThisWeekData: LiveData<List<EatingData>> = thisWeekData
    private val eatingInfo = MutableLiveData<EatingInfo>()
    val getEatingInfo = eatingInfo
    private val userPoints = MutableLiveData<UserPoints>()
    val getUserPoints = userPoints

    init {
        viewModelScope.launch {
            lastWeekData.value = FirebaseService.getEatingDataLastWeek(email)
            thisWeekData.value = FirebaseService.getEatingDataThisWeek(email)
            eatingInfo.value = FirebaseService.getEatingInfo(email)
            userPoints.value = FirebaseService.getUserPoints(email)
            eatingData.value = FirebaseService.getEatingData(email)?.map {
                it.toRecyclerItem()
            }

        }
    }

    fun insertEatingData(eatingData: EatingData) = viewModelScope.launch { FirebaseService.insertEatingData(eatingData,email) }
    fun insertEatingInfo(eatingInfo: EatingInfo) = viewModelScope.launch { FirebaseService.insertEatingInfo(eatingInfo,email) }
    fun insertUserPoints(userPoints: UserPoints) = FirebaseService.insertUserPoints(userPoints, email)
    fun insertPlannerDataTomorrow (plannerData: PlannerData) = viewModelScope.launch { FirebaseService.insertPlannerDataTomorrow(plannerData,email)}
    fun insertPlannerDataToday (plannerData: PlannerData) = viewModelScope.launch { FirebaseService.insertPlannerDataToday(plannerData,email)}

    private fun EatingData.toRecyclerItem() =
        RecyclerItem(this, R.layout.eating_item, BR.eatingData)

}