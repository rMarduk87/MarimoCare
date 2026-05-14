package rpt.tool.marimocare.utils.data.database.mappers.achievement

import rpt.tool.marimocare.utils.data.appmodels.Achievement
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.AchievementModel

class AchievementModelToAchievement : ModelMapper<AchievementModel, Achievement> {
    override val destination: Class<Achievement> = Achievement::class.java

    override fun map(source: AchievementModel): Achievement {
        return Achievement(
            id = source.id,
            code = source.code,
            titleID = source.titleId,
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
