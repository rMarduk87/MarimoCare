package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.marimocare.utils.data.AppModel
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.marimoqr.MarimoQRToMarimoQRModel
import rpt.tool.marimocare.utils.data.database.models.MarimoQRModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class MarimoQR(
    val code: Int,
    val marimoCode: Int,
    var qr: String,
    var validity: Boolean
): AppModel(), Serializable {

    init {
        addMapper(MarimoQRToMarimoQRModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == MarimoQRModel::class.java }.map(this) as T
    }
}