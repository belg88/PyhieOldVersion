    package gleb.apps.pyhie.mainMenu

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.okhttp.Challenge
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.*
import kotlinx.coroutines.launch

class MainMenuViewModel(private val email: String, val context: Context) : ViewModel() {

    private val _userData = MutableLiveData<UserPoints>()
    private val _userInfo = MutableLiveData<UserInfo>()
    private val _sleepInfo = MutableLiveData<SleepingInfo>()
    private val _eatingInfo = MutableLiveData<EatingInfo>()
    private val _challenges = MutableLiveData<List<gleb.apps.pyhie.challenges.Challenge>>()
    val getUserPoints: LiveData<UserPoints> = _userData
    val getEatingInfo:LiveData<EatingInfo> = _eatingInfo
    val getChallenges: LiveData<List<gleb.apps.pyhie.challenges.Challenge>> = _challenges




    fun insertUserInfo(userInfo: UserInfo) = FirebaseService.insertUserInfo(userInfo, email)
    fun insertUserPoints(userPoints: UserPoints) = FirebaseService.insertUserPoints(userPoints, email)

    init {
        viewModelScope.launch {
            _userData.value = FirebaseService.getUserPoints(email)
            _userInfo.value = FirebaseService.getUserInfo(email)
            _sleepInfo.value = FirebaseService.getSleepingPref(email)
            _eatingInfo.value = FirebaseService.getEatingInfo(email)
            _challenges.value = FirebaseService.getMainChallenges()
        }

    }
}