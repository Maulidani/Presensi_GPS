package com.apotekku.apotekku.session

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val PREFS_NAME = "PREFS_NAME"
    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun put(key: String, value: String) = editor.putString(key, value).apply()
    fun getString(key: String): String? = sharedPref.getString(key, null)
    fun put(key: String, value: Boolean) = editor.putBoolean(key, value).apply()
    fun getBoolean(key: String): Boolean = sharedPref.getBoolean(key, false)
    fun logout() = editor.clear().apply()

}