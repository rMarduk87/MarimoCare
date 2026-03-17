package rpt.tool.marimocare.utils.data.database.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.marimohealth.MarimoHealthScoreModelToMarimoHealthScore

@Keep
@Entity(tableName = "marimo_health_score")
class MarimoHealthScoreModel(
    @PrimaryKey
    @ColumnInfo(name = "code")
    val code: Int,
    @ColumnInfo(name = "marimo_code")
    val marimoCode: Int,
    @ColumnInfo(name = "date")
    val date: String?,
    @ColumnInfo(name = "health")
    val health: Int,
) : DbModel() {

    init {
        addMapper(MarimoHealthScoreModelToMarimoHealthScore())
    }
}