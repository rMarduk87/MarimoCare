package rpt.tool.marimocare.utils.data.database.mappers.achievement

import rpt.tool.marimocare.utils.data.appmodels.AchievementDetail
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.AchievementDetailModel

class AchievementDetailToAchievementDetailModel : ModelMapper<AchievementDetail, AchievementDetailModel> {
    override val destination: Class<AchievementDetailModel> = AchievementDetailModel::class.java

    override fun map(source: AchievementDetail): AchievementDetailModel {
        return AchievementDetailModel(
            id = source.id,
            achievement = source.achievement,
            type = source.type.id,
            description = source.description,
            typeDescription = source.type.description,
            unit = source.unit.id,
            unitDescription = source.unit.description,
            current = source.current,
            target = source.target
        )
    }
}
