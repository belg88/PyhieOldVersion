package gleb.apps.pyhie

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("1", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun saveString(key:String, value: String) {
        editor.putString(key,value)
        editor.apply()
    }
    fun getString(key: String) : String? {
       return sharedPref.getString(key, "")
    }
    fun saveInt(key:String, value: Int) {
        editor.putInt(key,value)
        editor.apply()
    }
    fun getInt(key: String) : Int? {
        return sharedPref.getInt(key, 0)
    }
    fun saveBoolean (key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun getBoolean (key: String):Boolean = sharedPref.getBoolean(key, true)

    fun saveLong(key:String, value:Long) {
        editor.putLong(key, value)
        editor.apply()
    }
    fun getLong(key: String): Long {
        return sharedPref.getLong(key, 0)
    }

}