package rpt.tool.marimocare.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.view.recyclerview.items.frequency.MarimoFrequencyItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import androidx.core.graphics.toColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.time.format.DateTimeParseException

class AppUtils {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun nextChange(
            date: String?,
            lastFrequencyChanges: Int,
        ): String {
            if (date.isNullOrBlank()) return ""
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val nextChange = LocalDate.parse(date, formatter)
                nextChange.plusDays(lastFrequencyChanges.toLong()).format(formatter)
            } catch (_: Exception) {
                ""
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun daysUntil(date: String?): Int {
            if (date.isNullOrBlank()) return 0
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val nextChange = LocalDate.parse(date, formatter)
                val today = LocalDate.now()
                ChronoUnit.DAYS.between(today, nextChange).toInt()
            } catch (_: Exception) {
                0
            }
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

        fun List<Marimo>.toMarimoItems(
            context: Context,
            color1: Int,
            color2: Int,
            bool: Boolean = false
        ): List<MarimoFrequencyItem> {
            return this.map { marimo ->

                val freqText = buildString { append(context.getString(R.string.every))
                    append(marimo.changeFrequencyDays)
                    append(" ")
                    append(context.getString(R.string.days))
                }
                val lastChangedText = marimo.lastChanged ?: "—"
                val notesText = marimo.notes ?: "No notes"

                val color = color1
                val background = color2

                MarimoFrequencyItem(
                    name = marimo.name,
                    frequencyDays = freqText,
                    frequency = marimo.changeFrequencyDays,
                    lastChanged = lastChangedText,
                    notes = notesText,
                    frequencyColor = color,
                    lastChangedColor = color,
                    cardBackgroundColor = background,
                    isMost = bool
                )
            }
        }

        fun getMonthLabels(months: Int): List<String> {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val labels = mutableListOf<String>()

            for (i in (months - 1) downTo 0) {
                val tempCal = calendar.clone() as Calendar
                tempCal.add(Calendar.MONTH, -i)
                labels.add(sdf.format(tempCal.time))
            }

            return labels
        }

        fun generateQRCode(marimo: Marimo?) : Bitmap {
            val safeName = URLEncoder.encode(marimo!!.name, "UTF-8")

            val deepLink =
                "rpt://tool.marimocare/open?code=${marimo.code}&name=$safeName"

            val bitMatrix: BitMatrix =
                QRCodeWriter().encode(deepLink,
                    BarcodeFormat.QR_CODE, 800, 800)

            val bmp = createBitmap(800, 800, Bitmap.Config.RGB_565)

            for (x in 0 until 800) {
                for (y in 0 until 800) {
                    bmp[x, y] =
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else
                            android.graphics.Color.WHITE
                }
            }

            return bmp
        }

        fun bitMapToString(bitmap: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun calcWaterChanges(
            lastChanged: String?,
            frequency: Int,
            registrationDate: String?
        ): List<String> {

            if (lastChanged.isNullOrBlank() || registrationDate.isNullOrBlank() || frequency <= 0) {
                return emptyList()
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val resultDates = mutableListOf<String>()

            try {
                val lastDate = LocalDate.parse(lastChanged, formatter)
                val regDate = LocalDate.parse(registrationDate, formatter)

                val hardLimitDate = LocalDate.of(2025, 11, 25)

                val stopDate = if (regDate.isAfter(hardLimitDate)) regDate else hardLimitDate

                if (lastDate.isBefore(stopDate)) {
                    return emptyList()
                }

                var calcDate = lastDate.minusDays(frequency.toLong())

                while (!calcDate.isBefore(stopDate)) {

                    resultDates.add(calcDate.format(formatter))

                    calcDate = calcDate.minusDays(frequency.toLong())
                }

            } catch (e: DateTimeParseException) {
                println("Errore nel parsing (formato atteso yyyy-MM-dd): ${e.message}")
                return emptyList()
            }

            return resultDates.sorted()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun calcWaterHealth(
            lastChanged: String?,
            frequency: Int,
            today: String?
        ): List<String> {
            if (lastChanged.isNullOrBlank() || today.isNullOrBlank() || frequency <= 0) {
                return emptyList()
            }

            val resultDates = mutableListOf<String>()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            try {
                val startDate = LocalDate.parse(lastChanged, formatter)
                val endDate = LocalDate.parse(today, formatter)

                var currentDate = startDate

                while (!currentDate.isAfter(endDate)) {
                    resultDates.add(currentDate.format(formatter))

                    currentDate = currentDate.plusDays(frequency.toLong())
                }
            } catch (e: DateTimeParseException) {
                e.printStackTrace()
            }

            return resultDates
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun calculateHealth(currentDate: String, lastWater: String): Int {
            if (currentDate.isBlank() || lastWater.isBlank()) return 0
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val current = LocalDate.parse(currentDate, formatter)
                val last = LocalDate.parse(lastWater, formatter)
                val daysBetween = ChronoUnit.DAYS.between(last, current).toInt()
                100 - daysBetween
            } catch (_: Exception) {
                0
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDifferenceBetweenDates(nextChange: String, currentDate: String): Int {
            if (nextChange.isBlank() || currentDate.isBlank()) return 0
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val next = LocalDate.parse(nextChange, formatter)
                val current = LocalDate.parse(currentDate, formatter)
                ChronoUnit.DAYS.between(current, next).toInt()
            } catch (_: Exception) {
                0
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getMonthsOfCare(logs: List<MarimoChange>): Int {
            if (logs.isEmpty()) return 0
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dates = logs.mapNotNull {
                it.waterChangeData?.let { dateStr ->
                    try {
                        LocalDate.parse(dateStr, formatter)
                    } catch (e: Exception) {
                        null
                    }
                }
            }.sorted()
            if (dates.isEmpty()) return 0
            return ChronoUnit.MONTHS.between(dates[0],
                LocalDate.now()).toInt()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDaysOfCare(logs: List<MarimoChange>): Int {
            if (logs.isEmpty()) return 0
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dates = logs.mapNotNull {
                it.waterChangeData?.let { dateStr ->
                    try {
                        LocalDate.parse(dateStr, formatter)
                    } catch (e: Exception) {
                        null
                    }
                }
            }.sorted()
            if (dates.isEmpty()) return 0
            return ChronoUnit.DAYS.between(dates[0], LocalDate.now()).toInt()
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
        const val STAT_PERIOD_SELECTED : String = "stat_period_selected"
        const val SHOW_FILTER_AND_SORT : String = "show_filter_and_sort"
        const val SHOW_MARIMO_BALLON : String = "show_marimo_balloon"
        const val SHOW_MARIMO_DASHBOARD_BALLON : String = "show_marimo_dashboard_balloon"
        const val SHOW_NEW_MARIMO_BALLON : String = "show_new_marimo_balloon"
        const val FIX : String = "fix_water_changes"
        const val MARIMO_OVERDUE_COUNTER : String = "marimo_overdue_counter"
        const val TAB_SELECTED : String = "stats_tab_selected"
        const val LAST_HEALTH_EXECUTION_DATE : String = "last_health_execution_date"
        const val SHOW_NEW_LOG_CHANGE_WATER : String = "show_new_log_change_water"
        const val SHOW_BALLON_NEW_STATS : String = "show_ballon-new_stats"
        const val SHOW_BALLON_NEW_POT_STATS : String = "show_ballon-new_pot_stats"
        const val SHOW_BALLON_FEEDBACK : String = "show_ballon-feedback"
        const val SHOW_ALERT_TODAY : String = "showAlertToday"
        const val ALERT_TODAY : String = "alertToday"
        const val LAST_DAILY_NOTIFICATION_DATE : String = "last_daily_notification_date"
        const val SHOW_NEW_SETTINGS_BALLOON : String = "show_new_settings_balloon"
        const val SHOW_ACHIEVEMENTS_DIALOG : String = "show_achievements_dialog"



        fun dpToPx(dp: Int): Int {
            return (dp * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
        }

    }
}