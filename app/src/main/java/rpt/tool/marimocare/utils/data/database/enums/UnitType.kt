package rpt.tool.marimocare.utils.data.database.enums

import androidx.annotation.Keep
import androidx.annotation.StringRes
import rpt.tool.marimocare.R

@Keep
enum class UnitType(val id: Int, @param:StringRes val descriptionResId: Int, val description: String) {

    WATERCHANGE(0, R.string.unit_type_water,"water change"),
    MARIMOS(1, R.string.unit_type_marimos, "marimos"),
    LOGS(2, R.string.unit_type_logs, "logs"),
    MILESTONES(3, R.string.unit_type_milestones, "milestones"),
    PHOTO(4, R.string.unit_type_photo, "photo"),
    NOTEDLOGS(5, R.string.unit_type_notedlogs, "noted logs"),
    MONTHS(6, R.string.unit_type_months, "months"),
    DAYS(7, R.string.unit_type_days, "days"),
    MARIMONAMED(8, R.string.unit_type_marimonamed, "marimo named"),
    UNIQUELYNAMED(9, R.string.unit_type_uniquelynamed, "uniquely named"),
    WITHPHOTO(10, R.string.unit_type_withphoto, "with photo"),
    NOTEWRITE(11, R.string.unit_type_notewrite, "note write");

    companion object {
        fun fromId(id: Int): UnitType = entries.firstOrNull { it.id == id } ?: MARIMOS
    }
}
