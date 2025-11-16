package rpt.tool.marimocare.utils.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.log.d

class NotifyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {

        d("NotifyWorker", "Worker START")

        val marimosToday = AlertDataUtils.getMarimosToNotifyToday()

        if (marimosToday.isNotEmpty()) {

            val names = marimosToday.joinToString(", ") { it.name }

            NotificationHelper(applicationContext).sendNotification(
                applicationContext.getString(R.string.today_marimo_list, names)
            )

            d("NotifyWorker", "Notifica inviata: $names")
        }

        return Result.success()
    }
}