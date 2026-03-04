package rpt.tool.marimocare.utils.view.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.marimocare.databinding.ItemHealthMarimoBinding
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScoreStats
import androidx.core.graphics.toColorInt
import rpt.tool.marimocare.R

class HealthMarimoAdapter : ListAdapter<MarimoHealthScoreStats,
        HealthMarimoAdapter.MarimoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarimoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHealthMarimoBinding.inflate(inflater, parent, false)
        return MarimoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarimoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MarimoViewHolder(
        private val binding: ItemHealthMarimoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MarimoHealthScoreStats) = with(binding) {

            marimoTitle.text = item.name
            healthValue.text = item.health.toString()
            frequencyText.text = buildString {
                append(item.frequency)
                append("d")
            }
            totalChangesText.text = item.totalChanges.toString()

            applyHealthStyle(item.health)
            applyHealthStyleToText(item.health)
        }

        private fun applyHealthStyle(health: Int) {

            val themeColorHex = when {
                health == 100 -> "#2E7D32"
                health in 40..70 -> "#FFFACD"
                health in 1..19 -> "#F57C00"
                health <= 0 -> "#C62828"
                else -> "#4CAF50"
            }

            val themeColor = themeColorHex.toColorInt()

            binding.healthContainer.backgroundTintList = ColorStateList.valueOf(themeColor)
            binding.marimoCard.setCardBackgroundColor(Color.WHITE)
            binding.marimoCard.strokeColor = themeColor
        }

        private fun applyHealthStyleToText(health: Int) {
            val textColorRes =
                if (health in 40..70) R.color.marimo_dark
                else android.R.color.white

            val color = binding.root.context.getColor(textColorRes)

            binding.healthValue.setTextColor(color)
            binding.healthText.setTextColor(color)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MarimoHealthScoreStats>() {
        override fun areItemsTheSame(oldItem: MarimoHealthScoreStats, newItem: MarimoHealthScoreStats):
                Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: MarimoHealthScoreStats, newItem: MarimoHealthScoreStats):
                Boolean {
            return oldItem == newItem
        }
    }
}