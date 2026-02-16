package rpt.tool.marimocare.utils.data.database.mappers.marimohealth

import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScore
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoHealthScoreModel


class MarimoHealthScoreToMarimoHealthScoreModel : ModelMapper<MarimoHealthScore, MarimoHealthScoreModel> {
    override val destination: Class<MarimoHealthScoreModel> = MarimoHealthScoreModel::class.java

    override fun map(source: MarimoHealthScore): MarimoHealthScoreModel {
        return MarimoHealthScoreModel(
            code = source.code,
            marimoCode = source.marimoCode,
            date = source.date,
            health = source.health,
        )
    }
}