package rpt.tool.marimocare.utils.data.appmodels

data class MarimoHealthScore(
    val id: Int,
    val name: String,
    val frequency: Int,
    val health: Int,
    val totalChanges: Int
)
