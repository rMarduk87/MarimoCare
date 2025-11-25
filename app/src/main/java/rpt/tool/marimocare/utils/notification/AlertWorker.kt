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
import rpt.tool.marimocare.utils.log.d
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager

class AlertWorker(appContext: Context, params: WorkerParameters) :
    Worker(appContext, params) {

    private val context = appContext

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {

        d("AlertWorker", "Worker START")

        val overdue = AlertDataUtils.getMarimosLate()
        val dueSoon = AlertDataUtils.getMarimosDueSoon(1)

        val overdueNames = overdue.joinToString(", ") { it.name }
        val soonNames = dueSoon.joinToString(", ") { it.name }

        SharedPreferencesManager.alertOverdue =
            if (overdue.isNotEmpty())
                context.getString(R.string.overdue_marimo, overdueNames)
            else ""

        SharedPreferencesManager.alertSoon =
            if (dueSoon.isNotEmpty())
                if(dueSoon.size==1)
                context.getString(R.string.soon_marimo_one, soonNames)
            else context.getString(R.string.soon_marimo, soonNames)
            else ""

        SharedPreferencesManager.showAlertOverdue = overdue.isNotEmpty()
        SharedPreferencesManager.showAlertSoon = dueSoon.isNotEmpty()

        d("AlertWorker", "Overdue: $overdueNames | Soon: $soonNames")

        return Result.success()
    }
}