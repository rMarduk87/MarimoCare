package rpt.tool.marimocare.utils.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import rpt.tool.marimocare.utils.AlertDataUtils

class NotifyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val notificationHelper = NotificationHelper(appContext)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val marimosToNotify = AlertDataUtils.marimosForNotification.value ?: emptyList()
        if (marimosToNotify.isNotEmpty()) {
            val names = marimosToNotify.joinToString(", ") { it.name }
            NotificationHelper(applicationContext)
                .sendNotification("Ciao sono una notifica...ma va? Marimo da controllare: $names")
        }
        return Result.success()
    }
}