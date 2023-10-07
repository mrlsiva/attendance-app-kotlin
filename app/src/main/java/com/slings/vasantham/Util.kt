package com.slings.vasantham

import android.content.Context
import android.content.SharedPreferences

object Util {
        /*
	 * Return shared preference value as string
	 */
    @JvmStatic
        fun getPreference(mContext: Context, key: String?, defaultValue: String?): String? {
            val preferences = mContext.getSharedPreferences(Common.Companion.appName, 0)
            return preferences.getString(key, defaultValue)
        }

        /*
	 * Store shared preference value as String.
	 */
    @JvmStatic
        fun setPreference(context: Context, key: String?, value: String?) {
            val preferences = getSharedPrefrenceInstance(context)
            val editor = preferences.edit()
            editor.putString(key, value).commit()
        }

    @JvmStatic
    fun getSharedPrefrenceInstance(mContext: Context): SharedPreferences {
            return mContext.getSharedPreferences(Common.Companion.appName, 0)
        }

    @JvmStatic
    fun setPreference(context: Context, key: String?, value: Boolean) {
            val preferences = getSharedPrefrenceInstance(context)
            val editor = preferences.edit()
            editor.putBoolean(key, value).commit()
        }

    @JvmStatic
    fun getPreference(mContext: Context, key: String?, defaultValue: Boolean): Boolean {
            val preferences = mContext.getSharedPreferences(Common.Companion.appName, 0)
            return preferences.getBoolean(key, defaultValue)
        }

    @JvmStatic
    fun setPreference(context: Context, key: String?, value: Int) {
            val preferences = getSharedPrefrenceInstance(context)
            val editor = preferences.edit()
            editor.putInt(key, value).commit()
        }

        @JvmStatic
        fun getPreference(mContext: Context, key: String?, defaultValue: Int): Int {
            val preferences = mContext.getSharedPreferences(Common.Companion.appName, 0)
            return preferences.getInt(key, defaultValue)
        }
    }