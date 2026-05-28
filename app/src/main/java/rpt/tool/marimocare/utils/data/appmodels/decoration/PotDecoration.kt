package rpt.tool.marimocare.utils.data.appmodels.decoration

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class PotDecoration(
    var id: String = java.util.UUID.randomUUID().toString(),
    var name: String = "",
    var type: String = "",
    var colour: String = "",
    var dimensions: String = "",
    var material: String = "",
    var notes: String = "",
    var isExpanded: Boolean = true
) : Serializable