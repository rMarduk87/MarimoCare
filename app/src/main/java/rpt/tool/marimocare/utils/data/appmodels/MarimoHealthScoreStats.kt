package rpt.tool.marimocare.utils.data.appmodels

data class MarimoHealthScoreStats(
    val id: Int,
    val name: String,
    val frequency: Int,
    val health: Int,
    val totalChanges: Int
)
