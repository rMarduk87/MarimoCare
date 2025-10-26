package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.marimocare.utils.data.AppModel
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.marimo.MarimoToMarimoModel
import rpt.tool.marimocare.utils.data.database.models.MarimoModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class Marimo(
    val code: Int,
    val name: String,
    val changeFrequencyDays: Int,
    val lastChanged: String?,
    val nextChange: String,
    val notes: String?,
    val daysLeft: Int
) : AppModel(), Serializable {

    init {
        addMapper(MarimoToMarimoModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == MarimoModel::class.java }.map(this) as T
    }
}