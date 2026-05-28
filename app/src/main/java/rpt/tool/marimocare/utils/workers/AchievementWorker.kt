package rpt.tool.marimocare.utils.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import rpt.com.base.log.d
import rpt.tool.marimocare.utils.managers.AchievementManager

class AchievementWorker(appContext: Context, params: WorkerParameters) :
    Worker(appContext, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        d("AchievementWorker", "Checking for new achievements...")
        return try {
            AchievementManager.Companion.recalculateAll(showDialogEarned = true)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}