package org.desperu.realestatemanager.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * Class to manage easily shared preferences.
 */
object MySharedPreferences {

    private const val SHARED_PREFS_FILE_NAME = "SharedPrefs"

    /**
     * Initialize shared preferences.
     *
     * @param context Context from MyPref is called.
     */
    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

    /**
     * Save string value in shared preferences file.
     *
     * @param context Context from MyPref is called.
     * @param key     Key for the value.
     * @param value   String value to save.
     */
    internal fun savePref(context: Context, key: String, value: String) =
        getPrefs(context).edit().putString(key, value).apply()

    /**
     * Get string value from shared preferences file.
     *
     * @param context  Context from MyPref is called.
     * @param key      Key of the value.
     * @param defValue Default value.
     * @return The corresponding value of the key.
     */
    internal fun getString(context: Context, key: String, defValue: String?): String? =
        getPrefs(context).getString(key, defValue)

    /**
     * Save integer value in shared preferences file.
     *
     * @param context Context from MyPref is called.
     * @param key     Key for the value.
     * @param value   Integer value to save.
     */
    internal fun savePref(context: Context, key: String, value: Int) =
        getPrefs(context).edit().putInt(key, value).apply()

    /**
     * Get integer value from shared preferences file.
     *
     * @param context  Context from MyPref is called.
     * @param key      Key of the value.
     * @param defValue Default value.
     * @return The corresponding value of the key.
     */
    internal fun getInt(context: Context, key: String, defValue: Int): Int =
        getPrefs(context).getInt(key, defValue)

    /**
     * Save long value in shared preferences file.
     *
     * @param context Context from MyPref is called.
     * @param key     Key for the value.
     * @param value   Long value to save.
     */
    internal fun savePref(context: Context, key: String, value: Long) =
        getPrefs(context).edit().putLong(key, value).apply()

    /**
     * Get long value from shared preferences file.
     *
     * @param context  Context from MyPref is called.
     * @param key      Key of the value.
     * @param defValue Default value.
     * @return The corresponding value of the key.
     */
    internal fun getLong(context: Context, key: String, defValue: Long): Long =
        getPrefs(context).getLong(key, defValue)

    /**
     * Save boolean value in shared preferences file.
     *
     * @param context Context from MyPref is called.
     * @param key     Key for the value.
     * @param value   The boolean value to save.
     */
    internal fun savePref(context: Context, key: String, value: Boolean) =
        getPrefs(context).edit().putBoolean(key, value).apply()

    /**
     * Get boolean value from shared preferences file.
     *
     * @param context  Context from MyPref is called.
     * @param key      Key of the value.
     * @param defValue Default value.
     * @return The corresponding value of the key.
     */
    internal fun getBoolean(context: Context, key: String, defValue: Boolean): Boolean =
        getPrefs(context).getBoolean(key, defValue)

    /**
     * Clear a specific value in shared preferences.
     *
     * @param context Context from MyPref is called.
     * @param key     Key of the value.
     */
    internal fun clear(context: Context, key: String) = getPrefs(context).edit().remove(key).apply()
}