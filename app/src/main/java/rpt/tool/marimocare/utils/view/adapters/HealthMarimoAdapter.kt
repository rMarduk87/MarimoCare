package rpt.tool.marimocare.utils.view.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.marimocare.databinding.ItemHealthMarimoBinding
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScoreStats
import androidx.core.graphics.toColorInt

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

    inner class MarimoViewHolder(
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
        }

        private fun applyHealthStyle(health: Int) {

            val (strokeColor, boxColor) = when {
                health == 100 -> {
                    Pair("#2E7D32", "#2E7D32")   // Verde
                }

                health in 40..70 -> {
                    Pair("#FFFACD", "#FFFACD")   // Giallo
                }

                health in 1..19 -> {
                    Pair("#F57C00", "#F57C00")   // Arancione
                }

                health <= 0 -> {
                    Pair("#C62828", "#C62828")   // Rosso
                }

                else -> {
                    Pair("#4CAF50", "#4CAF50")   // Default verde soft
                }
            }

            binding.marimoCard.strokeColor =
                strokeColor.toColorInt()

            binding.healthContainer.backgroundTintList =
                ColorStateList.valueOf(boxColor.toColorInt())
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