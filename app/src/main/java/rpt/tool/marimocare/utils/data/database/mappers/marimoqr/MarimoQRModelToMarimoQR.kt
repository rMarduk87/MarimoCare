package rpt.tool.marimocare.utils.data.database.mappers.marimoqr

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.appmodels.MarimoQR
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoChangeModel
import rpt.tool.marimocare.utils.data.database.models.MarimoQRModel

class MarimoQRModelToMarimoQR : ModelMapper<MarimoQRModel, MarimoQR> {
    override val destination: Class<MarimoQR> = MarimoQR::class.java

    @RequiresApi(Build.VERSION_CODES.O)
    override fun map(source: MarimoQRModel): MarimoQR {
        return MarimoQR(
            code = source.code,
            marimoCode = source.marimoCode,
            qr = source.marimoQRCode,
            validity = source.validity
        )
    }
}