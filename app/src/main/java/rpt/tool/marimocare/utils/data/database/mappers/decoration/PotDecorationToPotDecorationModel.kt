package rpt.tool.marimocare.utils.data.database.mappers.decoration

import rpt.tool.marimocare.utils.data.appmodels.PotDecoration
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.decoration.PotDecorationModel

class PotDecorationToPotDecorationModel : ModelMapper<PotDecoration, PotDecorationModel> {
    override val destination: Class<PotDecorationModel> = PotDecorationModel::class.java

    override fun map(source: PotDecoration): PotDecorationModel {
        return PotDecorationModel(
            id = source.id,
            marimoCode = source.marimoCode,
            name = source.name,
            type = source.type,
            colour = source.colour,
            dimensions = source.dimensions,
            material = source.material,
            notes = source.notes
        )
    }
}
