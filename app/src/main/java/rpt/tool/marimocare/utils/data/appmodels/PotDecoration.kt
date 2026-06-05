package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.marimocare.utils.data.AppModel
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.decoration.PotDecorationToPotDecorationModel
import rpt.tool.marimocare.utils.data.database.models.decoration.PotDecorationModel
import java.io.Serializable
import java.util.UUID

@Suppress("UNCHECKED_CAST")
@Keep
data class PotDecoration(
    var id: String = UUID.randomUUID().toString(),
    var marimoCode: Int = 0,
    var name: String = "",
    var type: String = "",
    var colour: String = "",
    var dimensions: String = "",
    var material: String = "",
    var notes: String = "",
    var isExpanded: Boolean = false
) : AppModel(), Serializable {

    init {
        addMapper(PotDecorationToPotDecorationModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == PotDecorationModel::class.java }.map(this) as T
    }
}
