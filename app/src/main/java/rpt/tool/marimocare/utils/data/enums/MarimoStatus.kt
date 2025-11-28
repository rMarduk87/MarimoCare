package rpt.tool.marimocare.utils.data.enums

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.AppUtils

@RequiresApi(Build.VERSION_CODES.O)
enum class MarimoStatus(
    val color: Int,
    val icon: Int,
    val daysLeftBackground: Int,
    val cardBackground: Int,
    val dropIcon: Int,
    val dropCircle: Int,
    val buttonChangeBg: Int,
    val buttonEditBg: Int,
    val buttonDeleteBg: Int,
    val notesCardBg: Int,
    val cardDateBg: Int,
) {
    NORMAL(
        color = R.color.marimo_item_green,
        icon = R.drawable.ic_clock,
        daysLeftBackground = R.drawable.bg_days_left_pill,
        cardBackground = R.drawable.bg_card_marimo,
        dropIcon = R.drawable.ic_water_drop_green,
        dropCircle = R.drawable.bg_circle_green,
        buttonChangeBg = R.drawable.bg_notes_green_card,
        buttonEditBg = R.drawable.bg_btn_edit_border,
        buttonDeleteBg = R.drawable.bg_btn_delete_border,
        notesCardBg = R.drawable.bg_notes_card,
        cardDateBg = R.drawable.bg_card_date
    ),
    DUE_SOON(
        color = R.color.marimo_orange,
        icon = R.drawable.ic_calendar_orange,
        daysLeftBackground = R.drawable.bg_days_left_pill_due_soon,
        cardBackground = R.drawable.bg_card_marimo_orange,
        dropIcon = R.drawable.ic_water_drop_orange,
        dropCircle = R.drawable.bg_circle_orange,
        buttonChangeBg = R.drawable.bg_orange_card,
        buttonEditBg = R.drawable.bg_btn_edit_border_orange,
        buttonDeleteBg = R.drawable.bg_btn_delete_border_orange,
        notesCardBg = R.drawable.bg_notes_card,
        cardDateBg = R.drawable.bg_card_date_orange
    ),
    OVERDUE(
        color = R.color.marimo_red,
        icon = R.drawable.ic_warning_triangle_red,
        daysLeftBackground = R.drawable.bg_days_left_pill_overdue,
        cardBackground = R.drawable.bg_card_marimo_red,
        dropIcon = R.drawable.ic_water_drop_red,
        dropCircle = R.drawable.bg_circle_red,
        buttonChangeBg = R.drawable.bg_red_card,
        buttonEditBg = R.drawable.bg_btn_edit_border_red,
        buttonDeleteBg = R.drawable.bg_btn_delete_border_red,
        notesCardBg = R.drawable.bg_notes_card,
        cardDateBg = R.drawable.bg_card_date_red

    );

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun from(daysLeft: Int): MarimoStatus {
            val isSoon = AppUtils.isDueSoon(daysLeft)
            return when {
                daysLeft < 0 && !isSoon -> OVERDUE
                daysLeft == 0 && isSoon -> DUE_SOON
                else -> NORMAL
            }
        }

        fun fromInt(index: Int): MarimoStatus {
            return when(index) {
                0 -> NORMAL
                1 -> DUE_SOON
                else -> OVERDUE
            }
        }
    }

    fun formatDaysLeftText(res: android.content.res.Resources, daysLeft: Int): String = when {
        daysLeft < 0 -> "${-daysLeft} ${res.getString(R.string.days_overdue)}"
        daysLeft > 0 -> "$daysLeft ${res.getString(R.string.days_left)}"
        else -> "${res.getString(R.string.due_in)} 0 ${res.getString(R.string.days)}"
    }
}