package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.marimocare.utils.data.AppModel
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.enums.AchievementType
import rpt.tool.marimocare.utils.data.database.enums.UnitType
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.achievement.AchievementDetailToAchievementDetailModel
import rpt.tool.marimocare.utils.data.database.models.AchievementDetailModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class AchievementDetail(
    val id: Int,
    val achievement: Int,
    val description: String,
    val type: AchievementType,
    val unit: UnitType,
    val current: Int,
    val target: Int
) : AppModel(), Serializable {

    init {
        addMapper(AchievementDetailToAchievementDetailModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == AchievementDetailModel::class.java }.map(this) as T
    }
}
