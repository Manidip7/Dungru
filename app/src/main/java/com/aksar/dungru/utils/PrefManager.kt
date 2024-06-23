package com.aksar.dungru.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringDef
import com.google.gson.Gson

class PrefManager private constructor(context: Context) {
    private val preferences: SharedPreferences
    private val gson = Gson()

    companion object {
        const val IS_LIGHT_MODE = "IS_LIGHT_MODE"
        const val USER_DATA = "USER_DATA"
        const val APP_STATE = "APP_STATE"
        const val TEMP_EMAIL = "TEMP_EMAIL"
        const val GENERAL_SETTING = "GENERAL_SETTING"
        const val NOTIFICATION_SETTING = "NOTIFICATION_SETTING"
        private const val PREF_NAME = "APP"
        private lateinit var instance: PrefManager

        @JvmStatic
        fun initialize(context: Context) {
            instance = PrefManager(context.applicationContext)
        }

        @JvmStatic
        fun get(): PrefManager {
            if (!::instance.isInitialized) {
                throw IllegalStateException("PrefManager is not initialized. Call initialize() before accessing instance.")
            }
            return instance
        }
    }

    init {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun save(@PrefKey key: String, value: Any?) {
        when (value) {
            is String -> preferences.edit().putString(key, value).apply()
            is Int -> preferences.edit().putInt(key, value).apply()
            is Long -> preferences.edit().putLong(key, value).apply()
            is Boolean -> preferences.edit().putBoolean(key, value).apply()
            is Any -> preferences.edit().putString(key, gson.toJson(value)).apply()
            else -> throw IllegalArgumentException("Unsupported value type")
        }
    }

    fun getString(key: String, defValue: String?): String? = preferences.getString(key, defValue)

    fun getBoolean(key: String, defValue: Boolean): Boolean = preferences.getBoolean(key, defValue)

    fun <T> getObject(key: String, objectClass: Class<T>): T? {
        val jsonString = preferences.getString(key, null) ?: return null
        return gson.fromJson(jsonString, objectClass)
    }

    fun clearPrefs() {
        preferences.edit().clear().apply()
    }

    fun removeKey(keyName: String) {
        preferences.edit().remove(keyName).apply()
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(IS_LIGHT_MODE, USER_DATA, APP_STATE , TEMP_EMAIL,GENERAL_SETTING, NOTIFICATION_SETTING)
    annotation class PrefKey
}
