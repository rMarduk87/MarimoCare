package rpt.tool.marimocare.utils.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.managers.RepositoryManager
import java.time.LocalDate
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager

class AlertWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {

        val marimosLate = AlertDataUtils.marimosForAlert.value ?: run {
            val marimos = RepositoryManager.marimoRepository.marimos.value ?: emptyList()
            val today = LocalDate.now()
            marimos.filter {
                AlertDataUtils.getNextChangeDate(it).isBefore(today)
            }
        }

        val names = marimosLate.joinToString(", ") { it.name }
        SharedPreferencesManager.alerts = if(marimosLate.isNotEmpty()) appContext.getString(
            R.string.marimo_in_ritardo,
            names
        )
        else ""
        SharedPreferencesManager.showAlert = marimosLate.isNotEmpty()
        return Result.success()
    }
}