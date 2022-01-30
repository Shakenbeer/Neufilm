package com.shakenbeer.neufilm.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

open class SharedPrefs(
    context: Context,
    prefName: String
) {
    private val prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)


    fun store(key: String, value: String) = editAndApplyData { it.putString(key, value) }
    fun store(key: String, value: Int) = editAndApplyData { it.putInt(key, value) }
    fun store(key: String, value: Long) = editAndApplyData { it.putLong(key, value) }
    fun store(key: String, value: Boolean) = editAndApplyData { it.putBoolean(key, value) }
    fun store(key: String, value: Set<String>) = editAndApplyData { it.putStringSet(key, value) }

    fun get(key: String, defaultValue: String) = prefs.getString(key, defaultValue) ?: ""
    fun get(key: String, defaultValue: Int) = prefs.getInt(key, defaultValue)
    fun get(key: String, defaultValue: Long) = prefs.getLong(key, defaultValue)
    fun get(key: String, defaultValue: Boolean) = prefs.getBoolean(key, defaultValue)
    fun get(key: String, defaultValue: Set<String>) =
        prefs.getStringSet(key, defaultValue) ?: defaultValue

    private fun editAndApplyData(customSetter: (SharedPreferences.Editor) -> Unit) {
        prefs.edit {
            customSetter(this)
            apply()
        }
    }
}