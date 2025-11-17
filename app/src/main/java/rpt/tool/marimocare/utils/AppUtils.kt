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

        fun indexOfContaining(input: String, items: List<String>): Int {
            return items.indexOfFirst { it.contains(input, ignoreCase = true) }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getCurrentDate() : String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now()
            return today.format(formatter)
        }

        const val USERS_SHARED_PREF : String = "user_pref"
        const val SHOW_ALERT_OVERDUE : String = "showAlertOverdue"
        const val SHOW_ALERT_SOON: String = "showAlertSoon"
        const val ALERT_OVERDUE: String = "alertOverdue"
        const val ALERT_SOON : String = "alertSoon"
        const val COLORED_IS_SELECTED: String = "colored_is_selected"
        const val TIPS_AUTO_SCROLL_SPEED : String = "tips_auto_scroll_speed"
        const val MARIMO_FILTER_SELECTED : String = "marimo_filter_selected"
        const val MARIMO_SORTING_SELECTED : String = "marimo_sorting_selected"



    }
}