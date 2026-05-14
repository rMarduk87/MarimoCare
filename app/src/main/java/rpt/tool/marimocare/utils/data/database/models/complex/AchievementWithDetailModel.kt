package rpt.tool.marimocare.utils.data.database.models.complex

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import rpt.tool.marimocare.utils.data.appmodels.AchievementComplex
import rpt.tool.marimocare.utils.data.appmodels.AchievementDetail
import rpt.tool.marimocare.utils.data.database.models.AchievementDetailModel
import rpt.tool.marimocare.utils.data.database.models.AchievementModel

@Keep
data class AchievementWithDetailModel(
    @Embedded val achievement: AchievementModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "achievement_id"
    )
    val details: List<AchievementDetailModel>
) {
    fun map(): AchievementComplex {
        return AchievementComplex(
            id = achievement.id,
            code = achievement.code,
            titleID = achievement.titleId,
            descriptionValue = achievement.descriptionValue,
            imageId = achievement.imageId,
            backgroundColor = achievement.backgroundColor,
            category = achievement.category,
            sortOrder = achievement.sortOrder,
            earned = achievement.earned,
            date = achievement.date,
            detail = details.first().map<AchievementDetail>()
        )
    }
}
