package rpt.tool.marimocare.utils.view

import android.content.Context
import androidx.core.content.ContextCompat
import rpt.tool.marimocare.databinding.StatsMarimoBinding

data class StatsCardConfig(
    val stringTitle: String,
    val stringValue: String,
    val iconRes: Int,
    val colorText: Int,
    val binding: StatsMarimoBinding,
    val subtitle: String,
    val unitText: String,
    val colorStroke: Int,
)


object StatsHelper {
    fun setUpStatsTopCard(requireContext: Context, listOf: List<StatsCardConfig>) {

        listOf.forEach { config ->
            config.binding.apply {
                tvTitle.text = config.stringTitle
                tvSubtitle.text = config.subtitle
                tvValue.text = config.stringValue
                tvUnit.text = config.unitText
                icon.setImageResource(config.iconRes)
                tvValue.setTextColor(
                    ContextCompat.getColor(requireContext, config.colorText))
                tvUnit.setTextColor(
                    ContextCompat.getColor(requireContext, config.colorText))
                cardAverageFrequency.strokeColor = ContextCompat.getColor(requireContext,
                    config.colorStroke)
            }
        }
    }
}