package rpt.tool.marimocare.utils.data.database.mappers.achievement

import rpt.tool.marimocare.utils.data.appmodels.AchievementDetail
import rpt.tool.marimocare.utils.data.database.enums.AchievementType
import rpt.tool.marimocare.utils.data.database.enums.UnitType
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.AchievementDetailModel

class AchievementDetailModelToAchievementDetail : ModelMapper<AchievementDetailModel, AchievementDetail> {
    override val destination: Class<AchievementDetail> = AchievementDetail::class.java

    override fun map(source: AchievementDetailModel): AchievementDetail {
        return AchievementDetail(
            id = source.id,
            achievement = source.achievement,
            description = source.description,
            type = AchievementType.fromId(source.type),
            unit = UnitType.fromId(source.unit),
            current = source.current,
            target = source.target
        )
    }
}
