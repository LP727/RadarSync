package com.example.radarsync.utilities

import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class TimeUtilities {
    companion object {
        fun getTimeAndDateStringsFromTimestamp(unixTimestamp: Long): Pair<String, String> {
            // Convert the timestamp to a Date object
            val date = Date(unixTimestamp) // Multiply by 1000 to convert to milliseconds

            // Format the time string
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val timeString = timeFormat.format(date)

            // Format the date string
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(date)

            return Pair(timeString, dateString)
        }
    }
}
