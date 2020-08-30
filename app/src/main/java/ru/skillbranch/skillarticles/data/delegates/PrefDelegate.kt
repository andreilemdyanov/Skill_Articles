package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? =
        findPreference(thisRef, defaultValue)

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        putPreference(thisRef, value)
    }

    private fun <T> findPreference(thisRef: PrefManager, default: T): T =
        with(thisRef.preferences) {
            val res = when (default) {
                is Boolean -> getBoolean(BOOLEAN, default)
                is String -> getString(STRING, default)
                is Float -> getFloat(FLOAT, default)
                is Int -> getInt(INT, default)
                is Long -> getLong(LONG, default)
                else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
            }
            res as T
        }

    private fun <T> putPreference(thisRef: PrefManager, value: T) =
        with(thisRef.preferences.edit()) {
            when (value) {
                is Long -> putLong(LONG, value)
                is String -> putString(STRING, value)
                is Int -> putInt(INT, value)
                is Boolean -> putBoolean(BOOLEAN, value)
                is Float -> putFloat(FLOAT, value)
                else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
            }.apply()
        }

    private companion object {
        const val BOOLEAN = "BOOLEAN"
        const val STRING = "STRING"
        const val FLOAT = "FLOAT"
        const val INT = "INT"
        const val LONG = "LONG"
    }
}