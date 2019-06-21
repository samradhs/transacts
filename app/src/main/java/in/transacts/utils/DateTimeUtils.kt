package `in`.transacts.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    private const val TAG = "DateTimeUtils"

    fun getDateFormatted(ts: Long): String {

        val dateStr = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
        return dateStr.format(Date(ts))
    }

    fun getTimeFormatted(ts: Long): String {

        val dateStr = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        return dateStr.format(Date(ts))
    }

    fun getMonthStartYearBack(): Long {

        val currentTimeInMillis = getCurrentTime()
        Log.e(TAG, "$currentTimeInMillis")

        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTimeInMillis

        cal.add(Calendar.YEAR, -1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        setToBeginningOfDay(cal)
        return cal.timeInMillis
    }

    fun getCurrentMonthStart(): Long {

        val currentTimeMillis = getCurrentTime()
        val cal                     = Calendar.getInstance()
        cal.timeInMillis = currentTimeMillis
        cal.set(Calendar.DAY_OF_MONTH, 1)
        setToBeginningOfDay(cal)
        return cal.timeInMillis
    }

    private fun getCurrentTime(): Long {

        return System.currentTimeMillis()
    }

    private fun setToBeginningOfDay(cal: Calendar) {

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }
}