package com.mani.wirup

import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Check if the selected date is today
    fun isToday(selectedDate: String): Boolean {
        val currentDate = dateFormat.format(Calendar.getInstance().time)
        return selectedDate == currentDate
    }

    // Check if the selected date is within the next 7 days
    fun isThisWeek(selectedDate: String): Boolean {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = dateFormat.parse(selectedDate)!!

        val diff = selectedCalendar.timeInMillis - currentDate.time
        val daysDifference = diff / (24 * 60 * 60 * 1000)

        return daysDifference in 0..6 // Within the next 7 days
    }
    fun isPastDate(selectedDate: String): Boolean {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = dateFormat.parse(selectedDate)!!

        return selectedCalendar.time.before(currentDate) // True if the date is in the past
    }
    fun addTaskToCalendar(context: android.content.Context, title: String, date: String, duration: Long): Boolean {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            calendar.time = dateFormat.parse(date)!!
            val startMillis = calendar.timeInMillis
            val endMillis = startMillis + (duration * 60 * 1000)

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, "Task scheduled via Wirup App")
                putExtra(CalendarContract.Events.EVENT_LOCATION, "No location specified")
                putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            }

            context.startActivity(intent)
            true // Return true if the intent was successfully started
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
            false // Return false if an error occurred
        }
    }
}


