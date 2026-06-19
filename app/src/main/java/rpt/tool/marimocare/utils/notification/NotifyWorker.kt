package rpt.tool.marimocare.utils.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.AppUtils
import rpt.com.base.log.d
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import java.time.LocalTime

class NotifyWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): androidx.work.ListenableWorker.Result {

        d("NotifyWorker", "Worker START")

        val notificationHelper = NotificationHelper(applicationContext)
        val todayDate = AppUtils.getCurrentDate()
        val currentHour = LocalTime.now().hour

        // Today Notifications - Fires every hour if water is not changed
        if (SharedPreferencesManager.showAlertToday) {
            val marimosToday = AlertDataUtils.getMarimosToNotifyToday()
            if (marimosToday.isNotEmpty()) {
                val names = marimosToday.joinToString(", ") { it.name }
                notificationHelper.sendNotification(
                    applicationContext.getString(R.string.water_change_today),
                    applicationContext.getString(R.string.today_marimo_list, names)
                )
            }
        }

        // Overdue and Due Soon Notifications - Once per day around 9:00 AM
        if (currentHour >= 9 && SharedPreferencesManager.lastDailyNotificationDate != todayDate) {
            
            // Overdue Notifications
            if (SharedPreferencesManager.showAlertOverdue) {
                val marimosLate = AlertDataUtils.getMarimosLate()
                if (marimosLate.isNotEmpty()) {
                    val names = marimosLate.joinToString(", ") { it.name }
                    notificationHelper.sendNotification(
                        applicationContext.getString(R.string.attention),
                        applicationContext.getString(R.string.overdue_marimo, names)
                    )
                }
            }

            // Due Soon Notifications
            if (SharedPreferencesManager.showAlertSoon) {
                val marimosSoon = AlertDataUtils.getMarimosDueSoon(1)
                if (marimosSoon.isNotEmpty()) {
                    val names = marimosSoon.joinToString(", ") { it.name }
                    val title = applicationContext.getString(R.string.due_soon)
                    val message = if (marimosSoon.size == 1)
                        applicationContext.getString(R.string.soon_marimo_one, names)
                    else
                        applicationContext.getString(R.string.soon_marimo, names)

                    notificationHelper.sendNotification(title, message)
                }
            }

            // Mark daily notifications as sent for today
            SharedPreferencesManager.lastDailyNotificationDate = todayDate
        }

        return androidx.work.ListenableWorker.Result.success()
    }
}