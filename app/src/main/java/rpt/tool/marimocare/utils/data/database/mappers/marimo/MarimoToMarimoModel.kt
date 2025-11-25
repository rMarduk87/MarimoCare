package rpt.tool.marimocare.utils.data.database.mappers.marimo

import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoModel

class MarimoToMarimoModel : ModelMapper<Marimo, MarimoModel> {
    override val destination: Class<MarimoModel> = MarimoModel::class.java

    override fun map(source: Marimo): MarimoModel {
        return MarimoModel(
            code = source.code,
            name = source.name,
            lastWaterChanges = source.lastChanged,
            frequencyChanges = source.changeFrequencyDays,
            notes = source.notes
        )
    }
}