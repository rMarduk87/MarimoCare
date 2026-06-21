package rpt.tool.marimocare.utils.managers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.MarimoCareApplication
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.AchievementComplex
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.utils.data.appmodels.PotDecoration

class AchievementManager {
    interface AchievementListener {
        fun onAchievementEarned(id: Int)
        fun onDataChanged()
    }

    companion object {
        private var listener: AchievementListener? = null
        private val achievementQueue = mutableListOf<Pair<Context, Int>>()
        private var isDialogShowing = false

        fun setListener(listener: AchievementListener?) {
            this.listener = listener
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun recalculateAll(showDialogEarned: Boolean = false,
                           userMeta: Map<String, Any> = emptyMap(),
                           context: Context = MarimoCareApplication.instance) {
            if (SharedPreferencesManager.showAchievement) return

            val achievement = RepositoryManager.marimoRepository.getAllAchievement()
            val marimos = RepositoryManager.marimoRepository.getAllSync()
            val waterChanges = RepositoryManager.marimoRepository.getAllChanges()
            val allDecorations = RepositoryManager.potDecorationRepository.getAllDecorations()
            calculateAchievement(context, achievement, marimos, waterChanges,
                showDialogEarned,
                userMeta,allDecorations)
            withContext(Dispatchers.Main) {
                listener?.onDataChanged()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private suspend fun calculateAchievement(
            context: Context,
            achievements: List<AchievementComplex>,
            marimos: List<Marimo>,
            waterChanges: List<MarimoChange>,
            showDialogEarned: Boolean,
            userMeta: Map<String, Any> = emptyMap(),
            allDecorations: List<PotDecoration>
        ) {

            val milestoneCount = waterChanges.count { it.isMilestone }
            val photoCount = waterChanges.count { !it.waterChangeImage.isNullOrEmpty() }
            val notedLogs = waterChanges.count { !it.waterChangesLog.isNullOrBlank() }
            val months = AppUtils.getMonthsOfCare(waterChanges)
            val days = AppUtils.getDaysOfCare(waterChanges)
            val uniquelyNamed = marimos.count {
                it.name.isNotBlank() && it.name.lowercase() != "marimo"
            }
            val withPhoto = marimos.count { !it.photo.isNullOrEmpty() }
            val freqs = marimos.map { it.changeFrequencyDays }.toSet().size
            val marimosWithNotes = marimos.count { !it.notes.isNullOrBlank() }

            val maxMonthsOfOwnership = marimos.mapNotNull { m ->
                m.registrationDate?.takeIf { it.isNotBlank() }?.let { dateStr ->
                    try {
                        ChronoUnit.MONTHS.between(
                            LocalDate.parse(dateStr),
                            LocalDate.now()
                        ).toInt()
                    } catch (_: Exception) {
                        null
                    }
                }
            }.maxOrNull() ?: 0

            val logsByDay = waterChanges.groupBy { it.waterChangeData }
            val speedyEarned = logsByDay.values.any { it.size >= 3 }

            val allUpToDate = marimos.isNotEmpty() && marimos.all { m ->
                val lastChange = m.lastChanged
                val frequency = m.changeFrequencyDays
                if (lastChange.isNullOrBlank() || frequency <= 0) {
                    false
                } else {
                    try {
                        val daysAgo = ChronoUnit.DAYS.between(
                            LocalDate.parse(lastChange),
                            LocalDate.now()
                        )
                        daysAgo <= frequency
                    } catch (_: Exception) {
                        false
                    }
                }
            }

            val potDecorationCount = allDecorations.count()

            val marimosWithPot = allDecorations.groupBy { it.marimoCode }.keys

            val totalEarnedCount = achievements.count { it.earned==1 }

            achievements.forEach { achievement ->
                if (achievement.earned == 1) return@forEach

                val current: Int? = when (achievement.code) {

                    "first_marimo" -> minOf(marimos.size, 1)
                    "duo" -> minOf(marimos.size, 2)
                    "trio" -> minOf(marimos.size, 3)
                    "collector" -> minOf(marimos.size, 5)
                    "marimo_hoarder" -> minOf(marimos.size, 10)
                    "marimo_army" -> minOf(marimos.size, 20)
                    "marimo_empire" -> minOf(marimos.size, 50)

                    "first_water_change" -> minOf(waterChanges.size, 1)
                    "consistent_5" -> minOf(waterChanges.size, 5)
                    "consistent_10" -> minOf(waterChanges.size, 10)
                    "consistent_25" -> minOf(waterChanges.size, 25)
                    "consistent_50" -> minOf(waterChanges.size, 50)
                    "consistent_100" -> minOf(waterChanges.size, 100)
                    "consistent_200" -> minOf(waterChanges.size, 200)
                    "consistent_500" -> minOf(waterChanges.size, 500)

                    "one_week_care" -> minOf(days, 7)
                    "one_month_care" -> minOf(months, 1)
                    "three_months_care" -> minOf(months, 3)
                    "six_months_care" -> minOf(months, 6)
                    "one_year_care" -> minOf(months, 12)
                    "two_years_care" -> minOf(months, 24)

                    "milestone_marker" -> minOf(milestoneCount, 1)
                    "five_milestones" -> minOf(milestoneCount, 5)
                    "ten_milestones" -> minOf(milestoneCount, 10)

                    "photo_keeper" -> minOf(photoCount, 1)
                    "photographer" -> minOf(photoCount, 5)
                    "photo_album" -> minOf(photoCount, 20)

                    "notes_taker" -> minOf(notedLogs, 5)
                    "journal_keeper" -> minOf(notedLogs, 20)
                    "chronicler" -> minOf(notedLogs, 50)

                    "named_marimo" -> minOf(uniquelyNamed, 1)
                    "creative_namer" -> minOf(uniquelyNamed, 3)

                    "profile_pic" -> minOf(withPhoto, 1)
                    "photogenic_collection" -> minOf(withPhoto, 5)

                    "varied_frequency" -> minOf(freqs, 3)

                    "senior_marimo" -> minOf(maxMonthsOfOwnership, 6)
                    "ancient_marimo" -> minOf(maxMonthsOfOwnership, 12)

                    "marimo_biographer_5" -> minOf(marimosWithNotes, 5)
                    "marimo_notes" -> if (marimosWithNotes > 0) 1 else 0

                    "on_time_streak_5" -> minOf(waterChanges.size, 5)
                    "on_time_streak_20" -> minOf(waterChanges.size, 20)
                    "never_overdue" -> if (allUpToDate) 1 else 0
                    "speedy" -> if (speedyEarned) 1 else 0

                    "early_bird" -> if (userMeta["earned_early_bird"] == true) 1 else null
                    "night_owl" -> if (userMeta["overdue_log_count"] == true) 1 else null
                    "feedback_giver" -> if (userMeta["submitted_feedback"] == true) 1 else null
                    "settings_explorer" -> if (userMeta["customized_settings"] == true) 1 else null
                    "stats_viewer" -> if (userMeta["visited_stats"] == true) 1 else null

                    "halfway_there" -> minOf(totalEarnedCount, 25)
                    "achievement_hunter" -> minOf(totalEarnedCount, 49)

                    "first_decoration"-> minOf(potDecorationCount, 1)
                    "decorator"-> minOf(marimosWithPot.size,3)
                    "interior_designer"-> minOf(potDecorationCount, 10)


                    else -> null
                }

                if (current != null) {
                    updateProgressForAchievement(achievement.id, current, showDialogEarned, context)
                }
            }
        }

        suspend fun deleteAllAchievement() {
            RepositoryManager.marimoRepository.resetAllAchievements()
            withContext(Dispatchers.Main) {
                listener?.onDataChanged()
            }
        }

        suspend fun earnAchievement(id: Int, date: String, showDialogEarned: Boolean,
                            context: Context = MarimoCareApplication.instance) {
            RepositoryManager.marimoRepository.earnAchievement(id, date)
            if (showDialogEarned) {
                showAchievementEarnedDialog(context, id)
            }
            withContext(Dispatchers.Main) {
                listener?.onAchievementEarned(id)
                listener?.onDataChanged()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun updateProgressForAchievement(id: Int, current: Int, showDialogEarned: Boolean,
                                         context: Context = MarimoCareApplication.instance) {
            val earned = RepositoryManager.marimoRepository.updateAchievementDetail(id, current)
            if (earned) {
                earnAchievement(id, AppUtils.getCurrentDate(), showDialogEarned, context)
            } else {
                withContext(Dispatchers.Main) {
                    listener?.onDataChanged()
                }
            }
        }

        private fun showAchievementEarnedDialog(context: Context, id: Int) {
            Handler(Looper.getMainLooper()).post {
                achievementQueue.add(Pair(context, id))
                if (!isDialogShowing) {
                    showNextAchievementDialog()
                }
            }
        }

        private fun showNextAchievementDialog() {
            if (achievementQueue.isEmpty()) {
                isDialogShowing = false
                return
            }

            isDialogShowing = true
            val (currentContext, id) = achievementQueue.removeAt(0)

            MarimoCareApplication.instance.applicationScope.launch(Dispatchers.IO) {
                val achievement = RepositoryManager.marimoRepository.getAllAchievement().find {
                    it.id == id }

                withContext(Dispatchers.Main) {
                    achievement?.let { ach ->
                        try {
                            if (currentContext !is Activity || currentContext.isFinishing || currentContext.isDestroyed) {
                                showNextAchievementDialog()
                                return@withContext
                            }

                            val inflater = LayoutInflater.from(currentContext)
                            val view = inflater.inflate(R.layout.dialog_achievement_earned,
                                null)

                            val title = view.findViewById<TextView>(R.id.txtAchievementTitle)
                            val icon = view.findViewById<TextView>(R.id.txtAchievementIcon)
                            val desc = view.findViewById<TextView>(R.id.txtAchievementDesc)
                            val btnOk = view.findViewById<Button>(R.id.btnOk)
                            val btnClose = view.findViewById<android.widget.ImageView>(R.id.btnClose)
                            val iconContainer = view.findViewById<android.view.View>(R.id.iconContainer)
                            val txtUnlocked = view.findViewById<TextView>(R.id.txtUnlocked)
                            val rootLayout = view.findViewById<android.view.View>(R.id.root_layout)

                            title.text = currentContext.getString(ach.titleID)
                            desc.text = currentContext.getString(ach.descriptionValue)
                            icon.text = currentContext.getString(ach.imageId)

                            try {
                                val color = ach.backgroundColor.toColorInt()
                                txtUnlocked.setTextColor(color)

                                val rootBg = rootLayout.background?.mutate() as?
                                        android.graphics.drawable.GradientDrawable
                                rootBg?.setStroke(AppUtils.dpToPx(1), color)

                                val iconBg = iconContainer.background?.mutate() as?
                                        android.graphics.drawable.GradientDrawable
                                iconBg?.setStroke(AppUtils.dpToPx(2), color)

                                val btnBg = btnOk.background?.mutate() as?
                                        android.graphics.drawable.GradientDrawable
                                btnBg?.setStroke(AppUtils.dpToPx(1), color)
                                btnOk.setTextColor(color)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            val dialog = AlertDialog.Builder(currentContext,
                                R.style.CustomDialogTheme)
                                .setView(view)
                                .setCancelable(true)
                                .create()


                            dialog.window?.setBackgroundDrawable(
                                android.graphics.Color.TRANSPARENT.toDrawable())

                            btnOk.setOnClickListener { dialog.dismiss() }
                            btnClose.setOnClickListener { dialog.dismiss() }

                            dialog.setOnDismissListener {
                                showNextAchievementDialog()
                            }

                            dialog.show()

                            val width = AppUtils.dpToPx(340)
                            dialog.window?.setLayout(width, android.view.ViewGroup
                                .LayoutParams.WRAP_CONTENT)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showNextAchievementDialog()
                        }
                    } ?: run {
                        showNextAchievementDialog()
                    }
                }
            }
        }
    }
}