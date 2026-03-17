package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.marimocare.utils.data.AppModel
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.marimo.MarimoToMarimoModel
import rpt.tool.marimocare.utils.data.database.mappers.marimohealth.MarimoHealthScoreToMarimoHealthScoreModel
import rpt.tool.marimocare.utils.data.database.models.MarimoHealthScoreModel
import rpt.tool.marimocare.utils.data.database.models.MarimoModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class MarimoHealthScore(
    val code: Int,
    var marimoCode: Int,
    var date: String?,
    var health: Int,
) : AppModel(), Serializable {

    init {
        addMapper(MarimoHealthScoreToMarimoHealthScoreModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == MarimoHealthScoreModel::class.java }.map(this) as T
    }
}