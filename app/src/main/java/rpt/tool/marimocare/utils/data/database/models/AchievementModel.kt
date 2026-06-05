package rpt.tool.marimocare.utils.data.database.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.achievement.AchievementModelToAchievement


@Keep
@Entity(tableName = "achievement")
class AchievementModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "title")
    val titleId: Int,
    @ColumnInfo(name = "description")
    val descriptionValue: Int,
    @ColumnInfo(name = "image")
    val imageId: Int,
    @ColumnInfo(name = "color")
    val backgroundColor: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "order")
    val sortOrder: Int,
    @ColumnInfo(name = "earned", defaultValue = "0")
    val earned: Int,
    @ColumnInfo(name = "acquired_date")
    val date: String?,
) : DbModel() {

    init {
        addMapper(AchievementModelToAchievement())
    }
}
