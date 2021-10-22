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

class BroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPref = SharedPref(context)
        sharedPref.saveBoolean(Keys.EAT1_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.EAT2_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.EAT3_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.EAT4_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.EAT5_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.SLEEP1_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.SLEEP2_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.SLEEP3_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.CLEAN_NOT_SUBMITTED,true)
        sharedPref.saveBoolean(Keys.PLANNER_NOT_SUBMITTED,true)
    }
}