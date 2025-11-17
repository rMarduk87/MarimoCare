package rpt.tool.marimocare.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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
    fun getMarimosLate(): List<Marimo> {
        return RepositoryManager.marimoRepository.getAllSync()
            .filter {
                val next = parse(it.nextChange)
                next.isBefore(LocalDate.now())
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMarimosDueSoon(days: Int): List<Marimo> {
        return RepositoryManager.marimoRepository.getAllSync()
            .filter {
                val next = parse(it.nextChange)
                val diff = ChronoUnit.DAYS.between(LocalDate.now(),
                    next)
                diff in 1..days
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMarimosToNotifyToday(): List<Marimo> {
        return RepositoryManager.marimoRepository.getAllSync()
            .filter {
                val next = parse(it.nextChange)
                next == LocalDate.now()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recalc() {
        val marimosLate = getMarimosLate()
        val marimosSoon = getMarimosDueSoon(2)

        SharedPreferencesManager.showAlertOverdue = marimosLate.isNotEmpty()
        SharedPreferencesManager.showAlertSoon = marimosSoon.isNotEmpty()

        SharedPreferencesManager.alertOverdue =
            marimosLate.joinToString(", ") { it.name }

        SharedPreferencesManager.alertSoon =
            marimosSoon.joinToString(", ") { it.name }
    }
}