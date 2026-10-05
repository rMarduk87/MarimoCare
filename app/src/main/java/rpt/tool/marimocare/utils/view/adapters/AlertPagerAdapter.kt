package rpt.tool.marimocare.utils.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.*
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import androidx.core.graphics.toColorInt
import kotlin.time.Duration.Companion.milliseconds

class AlertPagerAdapter(
    private var marimos: List<Marimo> = emptyList()
) : RecyclerView.Adapter<AlertPagerAdapter.AlertViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newMarimos: List<Marimo>) {
        marimos = newMarimos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert_jar,
            parent, false)
        return AlertViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(marimos[position])
    }

    override fun getItemCount(): Int = marimos.size

    override fun onViewRecycled(holder: AlertViewHolder) {
        super.onViewRecycled(holder)
        holder.cancelTimer()
    }

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.alertCard)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivAlertIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvMarimoName)
        private val tvStatusDesc: TextView = itemView.findViewById(R.id.tvStatusDesc)
        private val tvCountdown: TextView = itemView.findViewById(R.id.tvCountdown)
        private val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        private var timerJob: Job? = null
        private val waterLevelView: rpt.tool.marimocare.utils.view.animation.AnimatedWaterView =
            itemView.findViewById(R.id.waterLevelView)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(marimo: Marimo) {
            val context = itemView.context
            val status = MarimoStatus.from(marimo.daysLeft)

            tvName.text = marimo.name

            val colorRes = when (status) {
                MarimoStatus.OVERDUE -> R.color.marimo_red
                MarimoStatus.DUE_SOON -> R.color.marimo_orange
                MarimoStatus.NORMAL -> R.color.marimo_item_green
            }
            val mainColor = ContextCompat.getColor(context, colorRes)

            val safeBgColor = when (status) {
                MarimoStatus.OVERDUE -> "#FFF0F0".toColorInt()
                MarimoStatus.DUE_SOON -> "#FFF8E1".toColorInt()
                MarimoStatus.NORMAL -> "#E8F5E9".toColorInt()
            }

            card.setCardBackgroundColor(safeBgColor)
            card.strokeColor = mainColor

            ivIcon.imageTintList = ColorStateList.valueOf(mainColor)
            tvStatusDesc.setTextColor(mainColor)
            tvCountdown.setTextColor(mainColor)

            waterLevelView.setWaterColor(mainColor)

            when (status) {
                MarimoStatus.OVERDUE -> {
                    ivIcon.setImageResource(R.drawable.ic_warning_triangle_red)
                    tvStatusDesc.text = context.getString(R.string.overdue_by_19_days).
                    replace("19", "${marimo.daysLeft * -1}")
                    tvDueDate.text = buildString {
                        append(context.getString(R.string.was_due))
                        append(" ")
                        append(marimo.nextChange)
                    }
                    waterLevelView.setWaterPercentage(0.15f)
                }
                MarimoStatus.DUE_SOON -> {
                    ivIcon.setImageResource(R.drawable.ic_calendar_due_soon)
                    tvStatusDesc.text = context.getString(R.string.due_in)
                    tvDueDate.text = buildString {
                        append(context.getString(R.string.due))
                        append(" ")
                        append(marimo.nextChange)
                    }
                    waterLevelView.setWaterPercentage(0.15f)
                }
                MarimoStatus.NORMAL -> {
                    ivIcon.setImageResource(R.drawable.ic_water_drop_green)
                    tvStatusDesc.text = context.getString(R.string.next_change_in)
                    tvDueDate.text = buildString {
                        append(context.getString(R.string.due))
                        append(" ")
                        append(marimo.nextChange)
                    }

                    try {
                        val nextChangeDate = LocalDate.parse(marimo.nextChange)
                        val today = LocalDate.now()
                        val daysLeftActual = java.time.temporal.ChronoUnit.DAYS.between(
                            today, nextChangeDate).toFloat()
                        val frequency = marimo.changeFrequencyDays.toFloat()

                        var percentage = if (frequency > 0) daysLeftActual / frequency else 0f

                        if (percentage < 0.15f) percentage = 0.15f
                        if (percentage > 1f) percentage = 1f

                        waterLevelView.setWaterPercentage(percentage)
                    } catch (e: Exception) {
                        waterLevelView.setWaterPercentage(0.5f)
                    }
                }
            }

            startLiveTimer(marimo.nextChange, status, context)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun startLiveTimer(
            nextChangeDateStr: String,
            status: MarimoStatus,
            context: Context
        ) {
            timerJob?.cancel()
            timerJob = CoroutineScope(Dispatchers.Main).launch {
                while(isActive) {
                    try {
                        val nextChangeDate = LocalDate.parse(nextChangeDateStr)
                        val now = LocalDateTime.now()

                        val duration = if (status == MarimoStatus.OVERDUE) {
                            val dueDate = nextChangeDate.atStartOfDay()
                            Duration.between(dueDate, now)
                        } else {
                            val targetDate = if (!nextChangeDate.isAfter(LocalDate.now())) {
                                nextChangeDate.plusDays(1).atStartOfDay()
                            } else {
                                nextChangeDate.atStartOfDay()
                            }
                            Duration.between(now, targetDate)
                        }

                        val safeDuration = if (duration.isNegative) Duration.ZERO else duration

                        val days = safeDuration.toDays()
                        val hours = safeDuration.toHours() % 24
                        val mins = safeDuration.toMinutes() % 60
                        val secs = safeDuration.seconds % 60

                        val timeString = String.format(java.util.Locale.ROOT,
                            "%02d:%02d:%02d", hours, mins, secs)

                        when (status) {
                            MarimoStatus.OVERDUE -> {
                                tvCountdown.text = buildString {
                                    append("+")
                                    append(days)
                                    append(context.getString(R.string.day_format))
                                    append(" ")
                                    append(timeString)
                                }
                            }
                            MarimoStatus.DUE_SOON if days == 0L -> {
                                tvCountdown.text = timeString
                            }
                            else -> {
                                tvCountdown.text = buildString {
                                    append(days)
                                    append(context.getString(R.string.day_format))
                                    append(" ")
                                    append(timeString)
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                    delay(1000.milliseconds)
                }
            }
        }

        fun cancelTimer() {
            timerJob?.cancel()
            timerJob = null
        }
    }
}