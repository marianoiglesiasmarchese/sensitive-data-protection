package com.sensitive.info.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

object DateUtil {

    /**
     * This function allow us to convert a Date to a String. You can either indicate your custom pattern or
     * just use the default one "MM/dd/yyyy HH:mm:ss".
     */
    fun convertDateToString(date: Date, pattern: String = "MM/dd/yyyy HH:mm:ss"): String {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date according to the chosen pattern
        val df: DateFormat = SimpleDateFormat(pattern)
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        return df.format(date)
    }

    /**
     * This function allow us to convert a LocalDate to a String. You can either indicate your custom pattern or
     * just use the default one "MM/dd/yyyy HH:mm:ss".
     */
    fun convertDateToString(date: LocalDate, pattern: String = "MM/dd/yyyy HH:mm:ss"): String {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date according to the chosen pattern
        val df: DateFormat = SimpleDateFormat(pattern)
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        return df.format(date)
    }
}
