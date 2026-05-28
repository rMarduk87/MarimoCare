package rpt.tool.marimocare.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object AlertDataUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parse(date: String): LocalDate =
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextChange(marimo: Marimo): LocalDate {
        val lastChangeDate = parse(marimo.lastChanged ?: LocalDate.now().toString())
        return lastChangeDate.plusDays(marimo.changeFrequencyDays.toLong())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMarimosLate(): List<Marimo> {
        return RepositoryManager.marimoRepository.getAllSync()
            .filter {
                val next = calculateNextChange(it)
                next.isBefore(LocalDate.now())
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMarimosDueSoon(days: Int): List<Marimo> {
        return RepositoryManager.marimoRepository.getAllSync()
            .filter {
                val next = calculateNextChange(it)
                val diff = ChronoUnit.DAYS.between(LocalDate.now(),
                    next)
                diff in 1..days
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMarimosToNotifyToday(): List<Marimo> {
        return RepositoryManager.marimoRepository.getAllSync()
            .filter {
                val next = calculateNextChange(it)
                next.isEqual(LocalDate.now())
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recalc(context: Context) {
        val marimosLate = getMarimosLate()
        val marimosToday = getMarimosToNotifyToday()
        val marimosSoon = getMarimosDueSoon(1)

        val combinedSoon = marimosToday + marimosSoon

        val overdueNames = marimosLate.joinToString(", ") { it.name }
        val soonNames = combinedSoon.joinToString(", ") { it.name }

        SharedPreferencesManager.alertOverdueCounter = marimosLate.size

        SharedPreferencesManager.alertOverdue =
            if (marimosLate.isNotEmpty())
                context.getString(R.string.overdue_marimo, overdueNames)
            else ""

        SharedPreferencesManager.alertSoon =
            if (combinedSoon.isNotEmpty())
                if(combinedSoon.size == 1)
                    context.getString(R.string.soon_marimo_one, soonNames)
                else context.getString(R.string.soon_marimo, soonNames)
            else ""
    }
}