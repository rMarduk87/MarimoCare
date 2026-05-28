package rpt.tool.marimocare.utils.data.database.models.decoration

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.models.MarimoModel

@Keep
@Entity(
    tableName = "pot_decoration",
    foreignKeys = [
        ForeignKey(
            entity = MarimoModel::class,
            parentColumns = ["code"],
            childColumns = ["marimo_code"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["marimo_code"])]
)
data class PotDecorationModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "marimo_code")
    val marimoCode: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "colour")
    val colour: String,
    @ColumnInfo(name = "dimensions")
    val dimensions: String,
    @ColumnInfo(name = "material")
    val material: String,
    @ColumnInfo(name = "notes")
    val notes: String
) : DbModel()