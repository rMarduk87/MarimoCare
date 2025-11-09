package rpt.tool.marimocare.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.managers.RepositoryManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AlertDataUtils {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE


        @RequiresApi(Build.VERSION_CODES.O)
        fun getNextChangeDate(marimo: Marimo): LocalDate {
            val lastChanged = if (!marimo.lastChanged.isNullOrBlank()) {
                LocalDate.parse(marimo.lastChanged, formatter)
            } else {
                LocalDate.now()
            }

            return lastChanged.plusDays(marimo.changeFrequencyDays.toLong())
        }

        @RequiresApi(Build.VERSION_CODES.O)
        val marimosForNotification: LiveData<List<Marimo>> =
            RepositoryManager.marimoRepository.marimos.map { list ->
                val today = LocalDate.now()
                list.filter {
                    !it.lastChanged.isNullOrBlank() && getNextChangeDate(it)
                        .isAfter(today.plusDays(1))
                }
            }

        @RequiresApi(Build.VERSION_CODES.O)
        val marimosForAlert: LiveData<List<Marimo>> =
            RepositoryManager.marimoRepository.marimos.map { list ->
                val today = LocalDate.now()
                list.filter {
                    // This check is also safe now
                    !it.lastChanged.isNullOrBlank() && getNextChangeDate(it)
                        .isBefore(today)
                }
            }
    }
}