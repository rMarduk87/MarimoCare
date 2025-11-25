package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.marimocare.utils.data.AppModel
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.marimochanges.MarimoChangeToMarimoChangeModel
import rpt.tool.marimocare.utils.data.database.models.MarimoModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class MarimoChange(
    val code: Int,
    var coderMarimo: String,
    var waterChangeData: String?
) : AppModel(), Serializable {

    init {
        addMapper(MarimoChangeToMarimoChangeModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == MarimoModel::class.java }.map(this) as T
    }
}