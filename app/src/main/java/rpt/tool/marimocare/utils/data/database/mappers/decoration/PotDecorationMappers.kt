package rpt.tool.marimocare.utils.data.database.mappers.decoration

import rpt.tool.marimocare.utils.data.appmodels.decoration.PotDecoration
import rpt.tool.marimocare.utils.data.database.models.decoration.PotDecorationModel

object PotDecorationMappers {
    fun toModel(source: PotDecoration, marimoCode: Int): PotDecorationModel {
        return PotDecorationModel(
            id = source.id,
            marimoCode = marimoCode,
            name = source.name,
            type = source.type,
            colour = source.colour,
            dimensions = source.dimensions,
            material = source.material,
            notes = source.notes
        )
    }

    fun fromModel(source: PotDecorationModel): PotDecoration {
        return PotDecoration(
            id = source.id,
            name = source.name,
            type = source.type,
            colour = source.colour,
            dimensions = source.dimensions,
            material = source.material,
            notes = source.notes,
            isExpanded = false
        )
    }
}