package rpt.tool.marimocare.utils.data.database.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.database.mappers.marimo.MarimoModelToMarimo
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper

@Keep
@Entity(tableName = "marimo")
class MarimoModel(
    @PrimaryKey
    @ColumnInfo(name = "code")
    val code: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "last_water_changes")
    val lastWaterChanges: String?,
    @ColumnInfo(name = "frequency_changes")
    val frequencyChanges: Int,
    @ColumnInfo(name = "notes")
    val notes: String?
) : DbModel() {

    init {
        addMapper(MarimoModelToMarimo())
    }
}