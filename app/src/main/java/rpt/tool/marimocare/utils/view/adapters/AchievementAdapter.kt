package rpt.tool.marimocare.utils.view.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.appmodels.AchievementComplex
import androidx.core.graphics.toColorInt

import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils

class AchievementAdapter(
    private var achievements: List<AchievementComplex>
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newAchievements: List<AchievementComplex>) {
        this.achievements = newAchievements
        notifyDataSetChanged()
    }

    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvEarned: TextView = itemView.findViewById(R.id.tvEarned)
        private val tvEarnedDate: TextView = itemView.findViewById(R.id.tvEarnedDate)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvProgressLabel: TextView = itemView.findViewById(R.id.tvProgressLabel)
        private val tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(achievement: AchievementComplex) {
            val context = itemView.context

            tvTitle.text = context.getString(achievement.titleID)
            tvDescription.text = context.getString(achievement.descriptionValue)
            tvIcon.text = context.getString(achievement.imageId)

            val detail = achievement.detail
            val current = detail.current
            val target = detail.target
            val progress = if (target > 0) (current.toFloat() / target.toFloat() * 100).toInt() else 0

            val iconBackground = tvIcon.background as? GradientDrawable
            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()

            if (achievement.earned) {
                itemView.alpha = 1.0f

                val baseColor = try {
                    achievement.backgroundColor.toColorInt()
                } catch (e: Exception) {
                    Color.GRAY
                }

                tvIcon.paint.colorFilter = null

                val hsl = FloatArray(3)
                ColorUtils.colorToHSL(baseColor, hsl)
                hsl[2] = 0.95f
                val lightColor = ColorUtils.HSLToColor(hsl)

                cardView.setCardBackgroundColor(lightColor)
                cardView.strokeColor = baseColor

                iconBackground?.setColor(Color.WHITE)
                iconBackground?.setStroke(strokeWidth, baseColor)

                tvEarned.setTextColor(baseColor)
                tvEarnedDate.setTextColor(baseColor)
                tvEarnedDate.text = achievement.date ?: ""

                tvEarned.visibility = View.VISIBLE
                tvEarnedDate.visibility = if (achievement.date.isNullOrEmpty()) View.GONE
                else View.VISIBLE
                progressBar.progress = 100
                tvPercentage.text = context.getString(R.string._100)
                tvProgressLabel.text = buildString {
                                       append(target)
                                       append(" / ")
                                       append(target)
                                       append(" ")
                                       append(
                                           context.getString(
                                   detail.typeDescription)
                                      )
               }

            } else {
                itemView.alpha = 0.6f

                val bgColor = context.getColor(R.color.achievement_not_earned_bg)
                val strokeColor = context.getColor(R.color.achievement_not_earned_stroke)

                cardView.setCardBackgroundColor(bgColor)
                cardView.strokeColor = strokeColor

                iconBackground?.setColor(Color.WHITE)
                iconBackground?.setStroke(strokeWidth, strokeColor)

                tvIcon.paint.colorFilter = PorterDuffColorFilter(strokeColor,
                    PorterDuff.Mode.SRC_IN)

                tvEarned.visibility = View.GONE
                tvEarnedDate.visibility = View.GONE
                progressBar.progress = progress
                tvPercentage.text = buildString {
                    append(progress.toString())
                    append("%")
                }
                tvProgressLabel.text = buildString {
                                           append(current)
                                           append(" / ")
                                           append(target)
                                           append(" ")
                                           append(context.getString(detail.typeDescription))
               }
            }
        }
    }
}