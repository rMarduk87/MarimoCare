package rpt.tool.marimocare.utils.data.database.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.achievement.AchievementDetailModelToAchievementDetail
@Keep
@Entity(tableName = "achievement_details")
class AchievementDetailModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "achievement_id")
    val achievement: Int,
    @ColumnInfo(name = "desc")
    val description: String,
    @ColumnInfo(name = "type")
    val type: Int,
    @ColumnInfo(name = "type_desc")
    val typeDescription: String,
    @ColumnInfo(name = "unit")
    val unit: Int,
    @ColumnInfo(name = "unit_desc")
    val unitDescription: String,
    @ColumnInfo(name = "current")
    val current: Int,
    @ColumnInfo(name = "target")
    val target: Int
) : DbModel() {

    init {
        addMapper(AchievementDetailModelToAchievementDetail())
    }
}