package com.example.theaterparking.utils

import android.content.Context

object StorageUtils {
    fun putData(key: String, value: String, context: Context) {
        val editor = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String, context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

}