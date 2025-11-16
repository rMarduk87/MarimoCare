package rpt.tool.marimocare

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import rpt.tool.marimocare.utils.notification.AlertWorker
import rpt.tool.marimocare.utils.notification.NotificationHelper
import rpt.tool.marimocare.utils.notification.NotifyWorker
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class MarimoCareApplication : Application() {

    companion object {

        private lateinit var _instance: MarimoCareApplication

        val instance: MarimoCareApplication
            get() {
                return _instance
            }
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        //Init log
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper(this).createNotificationChannel()
        }

        scheduleAlertWorker(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            scheduleDailyNotify(this)
        }
    }

    private fun scheduleAlertWorker(context: Context) {

        val request = PeriodicWorkRequestBuilder<AlertWorker>(12,
            TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "AlertWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleDailyNotify(context: Context) {

        val now = LocalTime.now()
        val target = LocalTime.of(9, 0)

        val initialDelay = if (now.isAfter(target))
            Duration.between(now, target.plusHours(24))
        else
            Duration.between(now, target)

        val request = PeriodicWorkRequestBuilder<NotifyWorker>(24,
            TimeUnit.HOURS)
            .setInitialDelay(initialDelay)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "NotifyWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }


}