package rpt.tool.marimocare.utils.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.managers.HealthManager
import java.time.LocalDateTime
import java.time.Duration
import java.util.concurrent.TimeUnit

class MidnightHealthWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {

            val today = AppUtils.getCurrentDate()

            HealthManager()
                .calculateAndInsertHealthRobust(today)

            scheduleNext(applicationContext)

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun scheduleNext(context: Context) {

            val now = LocalDateTime.now()
            val nextMidnight = now.toLocalDate()
                .plusDays(1)
                .atStartOfDay()

            val delay = Duration.between(now, nextMidnight)
                .toMillis()

            val request =
                OneTimeWorkRequestBuilder<MidnightHealthWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiresBatteryNotLow(false)
                            .build()
                    )
                    .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "MidnightHealthWork",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }
}