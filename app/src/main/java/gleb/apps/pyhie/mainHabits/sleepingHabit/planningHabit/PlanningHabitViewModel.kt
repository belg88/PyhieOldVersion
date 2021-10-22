package gleb.apps.pyhie.mainHabits.sleepingHabit.planningHabit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gleb.apps.pyhie.BR
import gleb.apps.pyhie.R
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.EatingInfo
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingInfo
import gleb.apps.pyhie.recyclerview.RecyclerItem
import kotlinx.coroutines.launch

class PlanningHabitViewModel(private val email: String):ViewModel() {
    val plannerDataToday = MutableLiveData<List<RecyclerItem>>()
    val plannerDataTomorrow = MutableLiveData<List<RecyclerItem>>()
    val sleepingInfo = MutableLiveData<SleepingInfo>()
    val eatingData = MutableLiveData<EatingInfo>()

    fun insertPlannerDataTomorrow(plannerData: PlannerData) = viewModelScope.launch { FirebaseService.insertPlannerDataTomorrow(plannerData, email)}
    fun insertPlannerDataToday(plannerData: PlannerData) = viewModelScope.launch { FirebaseService.insertPlannerDataToday(plannerData, email)}
    fun deletePlannerDataTomorrow(plannerData: PlannerData) = viewModelScope.launch { FirebaseService.deletePlannerDataTomorrow(email, plannerData)}
    fun deletePlannerDataToday(plannerData: PlannerData) = viewModelScope.launch { FirebaseService.deletePlannerDataToday(email, plannerData)}


    init {
        viewModelScope.launch {

            plannerDataTomorrow.value = FirebaseService.getPlannerDataTomorrowList(email)?.map {
                it.toRecyclerItem()
            }
            plannerDataToday.value =  FirebaseService.getPlannerDataTodayList(email)?.map {
                it.toRecyclerItem()
            }
            sleepingInfo.value = FirebaseService.getSleepingPref(email)
            eatingData.value = FirebaseService.getEatingInfo(email)
        }

    }

    private fun PlannerData.toRecyclerItem() =
        RecyclerItem(this, R.layout.planner_item, BR.plannerData)

}