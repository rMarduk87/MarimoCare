package rpt.tool.marimocare.utils.data.appmodels

data class MarimoDetailUi(
    val marimoCode: Int,
    val name: String,
    val healthValue: Int?,
    val healthScoreString: String,
    val totalChanges: String,
    val frequencyDays: String,
    val daysTracked: String
)