package rpt.tool.marimocare.utils.data.appmodels


data class MarimoUpdate(
    val id: Int,
    val name: String,
    val overdueDays: Int,
    val lastChanged: String,
    var selected: Boolean = true
)
