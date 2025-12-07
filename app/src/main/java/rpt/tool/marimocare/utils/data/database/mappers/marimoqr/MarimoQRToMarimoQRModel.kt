package rpt.tool.marimocare.utils.data.database.mappers.marimoqr

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.appmodels.MarimoQR
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoChangeModel
import rpt.tool.marimocare.utils.data.database.models.MarimoQRModel

@Suppress("CAST_NEVER_SUCCEEDS")
class MarimoQRToMarimoQRModel : ModelMapper<MarimoQR, MarimoQRModel> {
    override val destination: Class<MarimoQRModel> = MarimoQRModel::class.java

    @RequiresApi(Build.VERSION_CODES.O)
    override fun map(source: MarimoQR): MarimoQRModel {
        return MarimoQRModel(
            code = source.code,
            marimoCode =  source.marimoCode,
            marimoQRCode = source.qr,
            validity = source.validity
        )
    }
}