package com.slings.vasantham.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */


data class AttendanceLogResponse(
    val success: Boolean,
    val message: String,
    val logs: LogData?
)

data class LogData(
    val current_page: Int,
    val data: List<AttendanceEntry>,
    // ... other fields
)

data class AttendanceEntry(
    val attandanceId: Int,
    val userId: Int,
    val startTime: String,
    val endTime: String?,
    val startDate: String,
    val endDate: String?
)
