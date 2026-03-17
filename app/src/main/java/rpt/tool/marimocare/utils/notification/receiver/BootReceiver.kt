package rpt.tool.marimocare.utils.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import rpt.tool.marimocare.utils.notification.AlertWorker
import rpt.tool.marimocare.utils.notification.NotifyWorker
import rpt.tool.marimocare.utils.workers.MidnightHealthWorker

class BootReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val alertRequest = OneTimeWorkRequest.Builder(AlertWorker::class.java)
                .build()

            val notifyRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                .build()

            WorkManager.getInstance(context)
                .enqueue(alertRequest)

            WorkManager.getInstance(context)
                .enqueue(notifyRequest)

            MidnightHealthWorker.scheduleNext(context)
        }
    }
}