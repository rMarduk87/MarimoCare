package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep

@Keep
data class AchievementComplex(
    val id: Int,
    val code: String,
    val titleID: Int,
    val descriptionValue: Int,
    val imageId: Int,
    val backgroundColor: String,
    val category: String,
    val sortOrder: Int,
    val earned: Int,
    val date: String?,
    val detail: AchievementDetail
)
