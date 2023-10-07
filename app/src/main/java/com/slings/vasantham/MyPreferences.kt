package com.slings.vasantham

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MyPreferences {

    private const val PREFS_NAME = "MyPrefs"
    private const val KEY_ARRAYLIST = "myArrayList"

    fun saveArrayList(context: Context, arrayList: ArrayList<item>) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(arrayList)
        editor.putString(KEY_ARRAYLIST, json)
        editor.apply()
    }

    fun getArrayList(context: Context): ArrayList<item> {
        val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString(KEY_ARRAYLIST, null)
        val type = object : TypeToken<ArrayList<item>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }
}
