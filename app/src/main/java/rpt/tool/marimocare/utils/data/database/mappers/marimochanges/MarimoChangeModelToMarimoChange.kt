package rpt.tool.marimocare.utils.data.database.mappers.marimochanges

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoChangeModel

class MarimoChangeModelToMarimoChange : ModelMapper<MarimoChangeModel, MarimoChange> {
    override val destination: Class<MarimoChange> = MarimoChange::class.java

    @RequiresApi(Build.VERSION_CODES.O)
    override fun map(source: MarimoChangeModel): MarimoChange {
        return MarimoChange(
            code = source.code,
            coderMarimo = source.marimoCode.toString(),
            waterChangeData = source.waterChangesData
        )
    }
}