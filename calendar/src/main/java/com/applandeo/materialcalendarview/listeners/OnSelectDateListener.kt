package com.applandeo.materialcalendarview.listeners

import java.util.*

/**
 * This interface is used to inform DatePicker that user selected any days
 */

interface OnSelectDateListener {

    @JvmSuppressWildcards
    fun onSelect(calendar: List<Calendar>)
}