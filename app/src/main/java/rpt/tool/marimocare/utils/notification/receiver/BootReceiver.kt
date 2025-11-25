package rpt.tool.marimocare.utils.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import rpt.tool.marimocare.utils.notification.AlertWorker
import rpt.tool.marimocare.utils.notification.NotifyWorker

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // Avvio subito l’AlertWorker per aggiornare overdue/soon
            val alertRequest = OneTimeWorkRequest.Builder(AlertWorker::class.java)
                .build()

            // Avvio la notifica dei marimo di oggi
            val notifyRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                .build()

            WorkManager.getInstance(context)
                .enqueue(alertRequest)

            WorkManager.getInstance(context)
                .enqueue(notifyRequest)
        }
    }
}