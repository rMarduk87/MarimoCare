package rpt.tool.marimocare.utils.managers

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.AppUtils.Companion.calculateHealth

class HealthManager {

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAndInsertHealthRobust(currentDay: String) {

        val marimos = RepositoryManager.marimoRepository.getAllSync()

        marimos.forEach { marimo ->

            val lastSavedDate =
                RepositoryManager.marimoRepository.
                    getLastHealthDate(marimo.code)

            val startDate = lastSavedDate

            val missingDates = AppUtils.calcWaterHealth(
                startDate,
                1,
                currentDay
            )

            missingDates.forEach { date ->

                val alreadyExists =
                    RepositoryManager.marimoRepository
                        .healthExists(marimo.code, date)

                if (alreadyExists == 0) {
                    val health = calculateHealth(
                        date,
                        marimo.lastChanged!!
                    )

                    RepositoryManager.marimoRepository
                        .addMarimoHealthScore(
                            marimo.code,
                            date,
                            health
                        )
                }
            }
        }
    }
}