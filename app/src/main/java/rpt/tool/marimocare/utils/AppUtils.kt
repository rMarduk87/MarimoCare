package rpt.tool.marimocare.utils

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.log.e
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

class AppUtils {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun nextChange(
            date: String?,
            lastFrequencyChanges: Int,
        ): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val nextChange = LocalDate.parse(date, formatter)
            val dataNext = nextChange.plusDays(lastFrequencyChanges.toLong()).format(formatter)
            return dataNext
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun daysUntil(date: String?): Int {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val nextChange = LocalDate.parse(date, formatter)
            val today = LocalDate.now()
            val left = ChronoUnit.DAYS.between(today, nextChange).toInt()
            return left
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun isDueSoon(dayleft:Int): Boolean{
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now()
            val dataNext = today.plusDays(dayleft.toLong()).format(formatter)
            return ChronoUnit.DAYS.between(today,
                LocalDate.parse(dataNext, formatter)).toInt() == 0
        }

        fun getMaxDate(): Long {
            val calendarToday = Calendar.getInstance()
            return calendarToday.timeInMillis
        }

        fun extractDay(string: String): Int {
            val regex = """\d+""".toRegex()
            val matchResult = regex.find(string)
            return matchResult?.value?.toIntOrNull() ?: 0
        }

        const val USERS_SHARED_PREF : String = "user_pref"
        const val ALERTS : String = "alerts"
        const val SHOW_ALERT: String = "show_alert"
        const val COLORED_IS_SELECTED: String = "colored_is_selected"


    }
}