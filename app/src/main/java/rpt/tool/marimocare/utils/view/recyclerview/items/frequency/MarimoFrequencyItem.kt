package rpt.tool.marimocare.utils.view.recyclerview.items.frequency

data class MarimoFrequencyItem(
    val name: String,
    val frequency: Int,
    val lastChanged: String,
    val notes: String,
    val frequencyColor: Int,
    val lastChangedColor: Int,
    val cardBackgroundColor: Int,
    val isMost: Boolean,
    val frequencyDays: String
)