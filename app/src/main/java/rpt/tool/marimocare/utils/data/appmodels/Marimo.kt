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
    var name: String,
    var changeFrequencyDays: Int,
    var lastChanged: String?,
    var nextChange: String,
    var notes: String?,
    var daysLeft: Int
) : AppModel(), Serializable {

    init {
        addMapper(MarimoToMarimoModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == MarimoModel::class.java }.map(this) as T
    }
}