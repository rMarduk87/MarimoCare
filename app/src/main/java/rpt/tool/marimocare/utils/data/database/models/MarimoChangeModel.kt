package rpt.tool.marimocare.utils.data.database.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.marimochanges.MarimoChangeModelToMarimoChange

@Keep
@Entity(tableName = "marimo_changes")
class MarimoChangeModel(
    @PrimaryKey
    @ColumnInfo(name = "code")
    val code: Int,
    @ColumnInfo(name = "marimo_code")
    val marimoCode: Int,
    @ColumnInfo(name = "water_changes_data")
    val waterChangesData: String?
) : DbModel() {

    init {
        addMapper(MarimoChangeModelToMarimoChange())
    }
}