package gleb.apps.pyhie.util

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import gleb.apps.pyhie.R
import android.content.BroadcastReceiver
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import gleb.apps.pyhie.MainActivity
import gleb.apps.pyhie.SharedPref
import kotlinx.coroutines.Job

class BroadcastReceiverNotificationsMealReminder : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (SharedPref(context).getBoolean(Keys.NOTIFICATIONS_ON)) {
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            when (GetTimes().hour) {
                in 0..11 -> notificationManager.sendNotification(
                    context.getText(R.string.its_time_for_breakfast_).toString(),
                    context
                )
                in 12..17-> notificationManager.sendNotification(
                    context.getText(R.string.its_time_for_lunch_).toString(),
                    context
                )
                in 18..24-> notificationManager.sendNotification(
                    context.getText(R.string.its_time_for_dinner_).toString(),
                    context
                )
            }


        }

    }
}