package rpt.tool.marimocare.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.appmodels.Marimo
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

        fun List<Marimo>.toMarimoItems(
            context: Context,
            color1: String,
            color2: String,
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

                val color = color1.toColorInt()
                val background = color2.toColorInt()

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

        fun getLastSixMonthsLabels(): List<String> {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val labels = mutableListOf<String>()

            for (i in 5 downTo 0) {
                val tempCal = calendar.clone() as Calendar
                tempCal.add(Calendar.MONTH, -i)
                labels.add(sdf.format(tempCal.time))
            }

            return labels
        }

        fun generateQRCode(marimo: Marimo?) : Bitmap {
            val deepLink = "marimocare://open?code=${marimo!!.code}&name=${marimo.name}"

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

        const val USERS_SHARED_PREF : String = "user_pref"
        const val SHOW_ALERT_OVERDUE : String = "showAlertOverdue"
        const val SHOW_ALERT_SOON: String = "showAlertSoon"
        const val ALERT_OVERDUE: String = "alertOverdue"
        const val ALERT_SOON : String = "alertSoon"
        const val COLORED_IS_SELECTED: String = "colored_is_selected"
        const val TIPS_AUTO_SCROLL_SPEED : String = "tips_auto_scroll_speed"
        const val MARIMO_FILTER_SELECTED : String = "marimo_filter_selected"
        const val MARIMO_SORTING_SELECTED : String = "marimo_sorting_selected"
        const val SHOW_FILTER_AND_SORT : String = "show_filter_and_sort"



    }
}