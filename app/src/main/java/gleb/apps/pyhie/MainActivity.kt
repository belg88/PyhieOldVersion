package gleb.apps.pyhie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import gleb.apps.pyhie.databinding.ActivityMainBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.util.AlarmService
import gleb.apps.pyhie.util.Keys
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var alarmService: AlarmService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkChallengeNumber(this)
        val navController = this.findNavController(R.id.fragmentContainer)
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        alarmService = AlarmService(this)
        createChannel(
            getString(R.string.notification_channel_one),
            getString(R.string.notification_name)
        )

        resetSleepEatAlarm {
            alarmService.setAlarmForSleepAndEatReset(it)
        }
        setAlarmForHabitReminder {
            alarmService.setAlarmForNotification(it)
        }


    }


    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,

                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun resetSleepEatAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.HOUR_OF_DAY, 23)
            this.set(Calendar.MINUTE, 59)
            callback(this.timeInMillis)
        }
    }

    private fun setAlarmForHabitReminder(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.HOUR_OF_DAY, 20)
            this.set(Calendar.MINUTE, 30)
            if (System.currentTimeMillis() >= this.timeInMillis) {
                this.set(Calendar.DAY_OF_YEAR,this.get(Calendar.DAY_OF_YEAR) + 1)
            }
            callback(this.timeInMillis)
        }
    }

    private fun checkChallengeNumber(context: Context) {
        lifecycleScope.launch {
            val userInfo = FirebaseService.getUserInfo(SharedPref(context).getString(Keys.EMAIL)!!)
            if (userInfo != null) {
                if (SharedPref(context).getInt(Keys.CURRENT_CHALLENGE) == 0) {
                    val challengeNumber = userInfo.currentChallenge
                    SharedPref(context).saveInt(Keys.CURRENT_CHALLENGE, challengeNumber)
                } else {
                    userInfo.currentChallenge =
                        SharedPref(context).getInt(Keys.CURRENT_CHALLENGE)!!
                }

                // check that balance matches sharedPref and firebase and vice versa.
                if (SharedPref(context).getInt(Keys.SLEEP_BALANCE) == 0) {
                    SharedPref(context).saveInt(Keys.SLEEP_BALANCE, userInfo.sleepBalance)
                } else {
                    userInfo.sleepBalance = SharedPref(context).getInt(Keys.SLEEP_BALANCE)!!
                }
                if (SharedPref(context).getInt(Keys.CLEAN_BALANCE) == 0) {
                    SharedPref(context).saveInt(Keys.CLEAN_BALANCE, userInfo.cleanBalance)
                } else {
                    userInfo.cleanBalance = SharedPref(context).getInt(Keys.CLEAN_BALANCE)!!
                }
                if (SharedPref(context).getInt(Keys.EAT_BALANCE) == 0) {
                    SharedPref(context).saveInt(Keys.EAT_BALANCE, userInfo.eatBalance)
                } else {
                    userInfo.eatBalance = SharedPref(context).getInt(Keys.EAT_BALANCE)!!
                }
                if (SharedPref(context).getInt(Keys.PLANNER_BALANCE) == 0) {
                    SharedPref(context).saveInt(Keys.PLANNER_BALANCE, userInfo.planningBalance)
                } else {
                    userInfo.planningBalance = SharedPref(context).getInt(Keys.PLANNER_BALANCE)!!
                }


                FirebaseService.insertUserInfo(
                    userInfo,
                    SharedPref(context).getString(Keys.EMAIL)!!)
            }



        }
    }


}