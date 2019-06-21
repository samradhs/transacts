package `in`.transacts.prefs

import android.content.Context

object SharedPref {

    private const val MESSAGE       = "message"
    private const val ALREADY_READ  = "already_read"

    fun saveMessagesRead(context: Context) {

        val sharedPreferences = context.getSharedPreferences(MESSAGE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(ALREADY_READ, true)
        editor.apply()
    }

    fun areMessagesRead(context: Context): Boolean {

        val sharedPreferences = context.getSharedPreferences(MESSAGE, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(ALREADY_READ, false)
    }
}