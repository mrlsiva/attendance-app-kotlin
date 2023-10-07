package com.slings.vasantham

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class item(val text: String, val value: Int, val date: Date, val time: Date) : Serializable {

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getFormattedTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm aa", Locale.getDefault())
        return timeFormat.format(time)
    }
}

