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
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.view.applyHealthColor
import rpt.tool.marimocare.utils.view.applyHealthStroke
import rpt.tool.marimocare.utils.view.applyHealthTextColor

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
                append(binding.root.context.getString(R.string.d))
            }
            totalChangesText.text = item.totalChanges.toString()

            healthContainer.applyHealthColor(item.health)
            marimoCard.setCardBackgroundColor(Color.WHITE)
            marimoCard.applyHealthStroke(item.health)

            healthValue.applyHealthTextColor(item.health)
            healthText.applyHealthTextColor(item.health)
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