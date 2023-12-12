package com.applandeo.materialcalendarview.exceptions

data class OutOfDateRangeException(override val message: String) : Exception(message)