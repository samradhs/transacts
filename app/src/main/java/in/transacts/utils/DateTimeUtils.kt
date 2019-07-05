package `in`.transacts.utils

import `in`.transacts.R
import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    private const val TAG = "DateTimeUtils"

    fun getDateFormatted(ts: Long): String {

        val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
        return dateFormat.format(Date(ts))
    }

    fun getTimeFormatted(ts: Long): String {

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        return timeFormat.format(Date(ts))
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
        val cal            = Calendar.getInstance()
        cal.timeInMillis = currentTimeMillis
        cal.set(Calendar.DAY_OF_MONTH, 1)
        setToBeginningOfDay(cal)
        return cal.timeInMillis
    }

    fun getMonthStamp(ts: Long): String {

        val monthStampFormat = SimpleDateFormat("yyyyMM", Locale.ENGLISH) // monthStamp is in this format to maintain some order when sorting
        return monthStampFormat.format(Date(ts))
    }

    fun getUIMonthFromMonthStamp(context: Context, monthStamp: String): String {

        val month = monthStamp.takeLast(2).toInt()
        val year = monthStamp.dropLast(2)
        val monthsArray = context.resources.getStringArray(R.array.months)
        return monthsArray[month - 1] + ", " + year.substring(2, 4)
    }

    fun getMonthStartEnd(monthStamp: String): Pair<Long, Long> {

        val month = monthStamp.takeLast(2).toInt()
        val year = monthStamp.dropLast(2).toInt()

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1) // because monthstamp represents JAN as 1, whereas in calendar JAN is represented by 0

        cal.set(Calendar.DAY_OF_MONTH, 1)
        setToBeginningOfDay(cal)
        val startTs = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        setToEndOfDay(cal)
        val endTs = cal.timeInMillis

        return Pair(startTs, endTs)
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

    private fun setToEndOfDay(cal: Calendar) {

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
    }
}