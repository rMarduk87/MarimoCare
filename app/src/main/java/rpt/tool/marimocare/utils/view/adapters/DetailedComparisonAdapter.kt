package rpt.tool.marimocare.utils.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.marimocare.databinding.ItemMarimoComparisonDetailBinding
import rpt.tool.marimocare.utils.data.appmodels.MarimoDetailUi
import androidx.core.content.ContextCompat
import rpt.tool.marimocare.R

class DetailedComparisonAdapter : RecyclerView.Adapter<DetailedComparisonAdapter.DetailViewHolder>() {

    private var detailsList: List<MarimoDetailUi> = emptyList()

    inner class DetailViewHolder(private val binding: ItemMarimoComparisonDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(detail: MarimoDetailUi) {
            val context = itemView.context

            binding.tvDetailName.text = detail.name
            binding.tvValueHealth.text = detail.healthScoreString
            binding.tvValueChanges.text = detail.totalChanges
            binding.tvValueFrequency.text = detail.frequencyDays
            binding.tvValueDays.text = detail.daysTracked

            applyHealthColor(context, detail.healthValue)
        }

        private fun applyHealthColor(context: Context, health: Int?) {
            if (health == null) {
                binding.tvValueHealth.setTextColor(ContextCompat.getColor(context,
                    R.color.text_title))
                return
            }

            val textColorId = if (health in 40..70) {
                R.color.marimo_dark
            } else {
                R.color.green_primary
            }

            binding.tvValueHealth.setTextColor(ContextCompat.getColor(context, textColorId))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemMarimoComparisonDetailBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(detailsList[position])
    }

    override fun getItemCount(): Int = detailsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<MarimoDetailUi>) {
        detailsList = newList
        notifyDataSetChanged()
    }
}