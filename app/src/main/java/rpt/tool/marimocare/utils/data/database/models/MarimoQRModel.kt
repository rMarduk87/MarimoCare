package rpt.tool.marimocare.utils.data.database.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.marimoqr.MarimoQRModelToMarimoQR

@Keep
@Entity(tableName = "marimo_qr")
class MarimoQRModel(
    @PrimaryKey
    @ColumnInfo(name = "code")
    val code: Int,
    @ColumnInfo(name = "marimo_code")
    val marimoCode: Int,
    @ColumnInfo(name = "marimo_qr_code")
    val marimoQRCode: String,
    @ColumnInfo(name = "validity")
    val validity: Boolean
) : DbModel() {

    init {
        addMapper(MarimoQRModelToMarimoQR())
    }
}