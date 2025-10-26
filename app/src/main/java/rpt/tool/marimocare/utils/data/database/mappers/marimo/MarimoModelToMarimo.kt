package rpt.tool.marimocare.utils.data.database.mappers.marimo

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoModel

class MarimoModelToMarimo : ModelMapper<MarimoModel, Marimo> {
    override val destination: Class<Marimo> = Marimo::class.java

    @RequiresApi(Build.VERSION_CODES.O)
    override fun map(source: MarimoModel): Marimo {
        return Marimo(
            code = source.code,
            name = source.name,
            changeFrequencyDays = source.frequencyChanges,
            notes = source.notes,
            lastChanged = source.lastWaterChanges,
            nextChange = AppUtils.nextChange(
                source.lastWaterChanges,
                source.frequencyChanges),
            daysLeft = AppUtils.daysUntil(
                source.lastWaterChanges)
        )
    }
}