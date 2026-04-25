package rpt.tool.marimocare.utils.data.database.mappers.achievement

import rpt.tool.marimocare.utils.data.appmodels.Achievement
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.AchievementModel

class AchievementToAchievementModel : ModelMapper<Achievement, AchievementModel> {
    override val destination: Class<AchievementModel> = AchievementModel::class.java

    override fun map(source: Achievement): AchievementModel {
        return AchievementModel(
            id = source.id,
            titleId = source.titleID,
            descriptionValue = source.descriptionValue,
            imageId = source.imageId,
            backgroundColor = source.backgroundColor,
            category = source.category,
            sortOrder = source.sortOrder,
            earned = source.earned,
            date = source.date
        )
    }
}
