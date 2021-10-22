package gleb.apps.pyhie.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import gleb.apps.pyhie.MainActivity
import gleb.apps.pyhie.R

private val NOTIFICATION_ID = 0


fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val image = BitmapFactory.decodeResource(
        applicationContext.resources, R.drawable.logo_pyhie
    )
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_one)
    ).setSmallIcon(R.drawable.ic_baseline_thumb_up_24)
        .setContentTitle("PYHIE")
        .setContentText(messageBody)
        .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID,builder.build())

    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }






}