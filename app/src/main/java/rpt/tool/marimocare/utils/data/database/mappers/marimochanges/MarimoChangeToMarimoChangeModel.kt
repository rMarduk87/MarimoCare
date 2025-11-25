package rpt.tool.marimocare.utils.data.database.mappers.marimochanges

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoChangeModel

@Suppress("CAST_NEVER_SUCCEEDS")
class MarimoChangeToMarimoChangeModel : ModelMapper<MarimoChange, MarimoChangeModel> {
    override val destination: Class<MarimoChangeModel> = MarimoChangeModel::class.java

    @RequiresApi(Build.VERSION_CODES.O)
    override fun map(source: MarimoChange): MarimoChangeModel {
        return MarimoChangeModel(
            code = source.code,
            marimoCode =  source.coderMarimo.toInt(),
            waterChangesData = source.waterChangeData
        )
    }
}