package gleb.apps.pyhie.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import gleb.apps.pyhie.challenges.Challenge
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit.CleaningData
import gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit.CleaningInfo
import gleb.apps.pyhie.mainHabits.sleepingHabit.planningHabit.PlannerInfo
import gleb.apps.pyhie.pojos.*
import gleb.apps.pyhie.util.GetDates
import kotlinx.coroutines.tasks.await

object FirebaseService {

    private val lastWeek = GetDates().weekNumber - 1
    var fb = FirebaseFirestore.getInstance()

    fun insertUserPoints(userPoints: UserPoints, email: String) {
        try {
            fb.collection(email).document(Keys.USER_POINTS).set(userPoints)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getUserPoints(email: String): UserPoints? {
        return try {
            fb.collection(email).document(Keys.USER_POINTS).get().await()
                .toObject(UserPoints::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    fun insertSleepingData(sleepingData: SleepingData, email: String) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.SLEEP_DATA)
                .add(sleepingData)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getSleepingData(email: String): List<SleepingData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.SLEEP_DATA)
                .orderBy("timeStamp", Query.Direction.DESCENDING).get().await()
                .toObjects(SleepingData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getSleepingDataThisWeek(email: String): List<SleepingData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.SLEEP_DATA)
                .whereEqualTo("weekNumber", GetDates().weekNumber).get().await()
                .toObjects(SleepingData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getSleepingDataLastWeek(email: String): List<SleepingData>? {
        if (lastWeek == 0) lastWeek == 52
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.SLEEP_DATA)
                .whereEqualTo("weekNumber", lastWeek).get().await()
                .toObjects(SleepingData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    fun insertUserInfo(userInfo: UserInfo, email: String) {
        try {
            fb.collection(email).document(Keys.INFO).set(userInfo)

        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getUserInfo(email: String): UserInfo? {
        return try {
            fb.collection(email).document(Keys.INFO).get().await().toObject(UserInfo::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    fun insertSleepingPref(sleepingInfo: SleepingInfo, email: String) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.SLEEP_INFO)
                .document(Keys.SLEEP_INFO).set(sleepingInfo)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getSleepingPref(email: String): SleepingInfo? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.SLEEP_INFO)
                .document(Keys.SLEEP_INFO).get().await()
                .toObject(SleepingInfo::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun insertEatingData(eatingData: EatingData, email: String) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.EATING_DATA)
                .add(eatingData).await()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getEatingData(email: String): List<EatingData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.EATING_DATA)
                .orderBy("timeStamp", Query.Direction.DESCENDING).get().await()
                .toObjects(EatingData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getEatingDataThisWeek(email: String): List<EatingData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.EATING_DATA)
                .whereEqualTo("weekNumber", GetDates().weekNumber).get().await()
                .toObjects(EatingData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getEatingDataLastWeek(email: String): List<EatingData>? {
        if (lastWeek == 0) lastWeek == 52
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.EATING_DATA)
                .whereEqualTo("weekNumber", lastWeek).get().await()
                .toObjects(EatingData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun insertEatingInfo(eatingInfo: EatingInfo, email: String) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.EATING_INFO)
                .document(Keys.EATING_INFO).set(eatingInfo).await()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getEatingInfo(email: String): EatingInfo? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.EATING_INFO)
                .document(Keys.EATING_INFO).get().await()
                .toObject(EatingInfo::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun insertPlannerDataTomorrow(
        plannerData: PlannerData,
        email: String
    ) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_DAT_TOM)
                .document(
                    plannerData.title + " " + plannerData.identifier
                )
                .set(plannerData).await()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun insertPlannerDataToday(
        plannerData: PlannerData,
        email: String
    ) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_DATA_TODAY)
                .document(plannerData.title + " " + plannerData.identifier)
                .set(plannerData).await()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getPlannerDataTomorrowList(email: String): List<PlannerData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_DAT_TOM)
                .orderBy("startTimeDouble", Query.Direction.ASCENDING).get().await()
                .toObjects(PlannerData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getPlannerDataTodayList(email: String): List<PlannerData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_DATA_TODAY)
                .orderBy("startTimeDouble", Query.Direction.ASCENDING).get().await()
                .toObjects(PlannerData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun deletePlannerDataTomorrow(email: String, plannerData: PlannerData) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_DAT_TOM)
                .document(plannerData.title + " " + plannerData.identifier)
                .delete().await()

        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun deletePlannerDataToday(email: String, plannerData: PlannerData) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_DATA_TODAY)
                .document(plannerData.title + " " + plannerData.identifier)
                .delete().await()

        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun insertPlannerInfo(
        plannerInfo: PlannerInfo,
        email: String
    ) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_INFO)
                .document(Keys.PLANNER_INFO)
                .set(plannerInfo).await()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getPlannerInfo(
        email: String
    ): PlannerInfo? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.PLANNER_INFO)
                .document(Keys.PLANNER_INFO)
                .get().await().toObject(PlannerInfo::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun insertCleaningInfo(
        cleaningInfo: CleaningInfo,
        email: String
    ) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.CLEANING_INFO)
                .document(Keys.CLEANING_INFO)
                .set(cleaningInfo).await()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getCleaningInfo(
        email: String
    ): CleaningInfo? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.CLEANING_INFO)
                .document(Keys.CLEANING_INFO)
                .get().await().toObject(CleaningInfo::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }


    suspend fun insertCleaningData(
        cleaningData: CleaningData,
        email: String
    ) {
        try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.CLEANING_DATA)
                .add(cleaningData).await()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    suspend fun getCleaningData(email: String): List<CleaningData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.CLEANING_DATA)
                .orderBy("timeStamp", Query.Direction.DESCENDING).get().await()
                .toObjects(CleaningData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getCleaningDataThisWeek(email: String): List<CleaningData>? {
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.CLEANING_DATA)
                .whereEqualTo("weekNumber", GetDates().weekNumber).get().await()
                .toObjects(CleaningData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getCleaningDataLastWeek(email: String): List<CleaningData>? {
        if (lastWeek == 0) lastWeek == 52
        return try {
            fb.collection(email).document(Keys.MAIN_HABITS).collection(
                Keys.CLEANING_DATA)
                .whereEqualTo("weekNumber", lastWeek).get().await()
                .toObjects(CleaningData::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }

    suspend fun getMainChallenges(): List<Challenge>? {
        return try {
            fb.collection("Challenges")
                .orderBy("number", Query.Direction.ASCENDING).get().await()
                .toObjects(Challenge::class.java)
        } catch (e: Exception) {
            e.stackTrace
            return null
        }
    }
}


