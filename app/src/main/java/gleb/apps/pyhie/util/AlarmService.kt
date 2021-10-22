package gleb.apps.pyhie.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmService (private val context:Context) {
    private val REQUEST_CODE = 0
    private val alarmManager: AlarmManager? = context.getSystemService(android.content.Context.ALARM_SERVICE) as AlarmManager?

    fun setAlarmForSleepAndEatReset (timeInMillis: Long) {
        setAlarm(timeInMillis,
        getPendingIntent(
            getIntent()
        ))
    }

    fun setAlarmForNotification (timeInMillis: Long) {
        setAlarm(timeInMillis,
        getPendingIntentNotification(
            getIntentNotification()
        ))
    }

    fun setAlarmForNotificationMeal1 (timeInMillis: Long) {
        setAlarm(timeInMillis,
        getPendingIntentNotificationMeal1(
            getIntentNotificationMeals()
        ))
    }

    fun setAlarmForNotificationMeal2 (timeInMillis: Long) {
        setAlarm(timeInMillis,
            getPendingIntentNotificationMeal2(
                getIntentNotificationMeals()
            ))
    }
    fun setAlarmForNotificationMeal3 (timeInMillis: Long) {
        setAlarm(timeInMillis,
            getPendingIntentNotificationMeal3(
                getIntentNotificationMeals()
            ))
    }
    fun setAlarmForNotificationMeal4 (timeInMillis: Long) {
        setAlarm(timeInMillis,
            getPendingIntentNotificationMeal4(
                getIntentNotificationMeals()
            ))
    }
    fun setAlarmForNotificationMeal5 (timeInMillis: Long) {
        setAlarm(timeInMillis,
            getPendingIntentNotificationMeal5(
                getIntentNotificationMeals()
            ))
    }



    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setRepeating(
                    AlarmManager.RTC,
                    timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        }
    }

    private fun getIntent() : Intent = Intent(context, BroadcastReceiver::class.java)
    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,REQUEST_CODE,intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    private fun getIntentNotification() : Intent = Intent(context, BroadcastReceiverNotifications::class.java)
    private fun getPendingIntentNotification(intent: Intent) =
        PendingIntent.getBroadcast(
            context,2,intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
private fun getIntentNotificationMeals() : Intent = Intent(context, BroadcastReceiverNotificationsMealReminder::class.java)
    private fun getPendingIntentNotificationMeal1(intent: Intent) =
        PendingIntent.getBroadcast(
            context,3,intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    private fun getPendingIntentNotificationMeal2(intent: Intent) =
        PendingIntent.getBroadcast(
            context,4,intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    private fun getPendingIntentNotificationMeal3(intent: Intent) =
        PendingIntent.getBroadcast(
            context,5,intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    private fun getPendingIntentNotificationMeal4(intent: Intent) =
        PendingIntent.getBroadcast(
            context,6,intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    private fun getPendingIntentNotificationMeal5(intent: Intent) =
        PendingIntent.getBroadcast(
            context,3,intent, PendingIntent.FLAG_UPDATE_CURRENT
        )





}