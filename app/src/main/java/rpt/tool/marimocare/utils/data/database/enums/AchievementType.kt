package rpt.tool.marimocare.utils.data.database.enums

import androidx.annotation.Keep
import androidx.annotation.StringRes
import rpt.tool.marimocare.R

@Keep
enum class AchievementType(val id: Int, @param:StringRes val descriptionResId: Int, val description: String) {
    MARIMO(1, R.string.ach_type_marimo, "marimo"),
    MARIMOS(2, R.string.ach_type_marimos, "marimos"),
    WATERCHANGE(3, R.string.ach_type_waterchange, "water change"),
    DAYSOFCARE(4, R.string.ach_type_daysofcare, "days of care"),
    MONTHSOFCARE(5, R.string.ach_type_monthsofcare, "months of care"),
    MILESTONE(6, R.string.ach_type_milestone, "milestone"),
    PHOTO(7, R.string.ach_type_photo, "photo"),
    NOTE(8, R.string.ach_type_note, "note"),
    NAME(9, R.string.ach_type_name, "name"),
    MARIMOWITHPHOTO(10, R.string.ach_type_marimowithphoto, "marimo with photo"),
    LOGS(11, R.string.ach_type_logs, "logs");

    companion object {
        fun fromId(id: Int): AchievementType = entries.firstOrNull { it.id == id } ?: MARIMO
    }
}
