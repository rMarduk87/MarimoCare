package rpt.tool.marimocare.utils.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import rpt.tool.marimocare.MarimoCareApplication
import rpt.tool.marimocare.utils.AppUtils


object SharedPreferencesManager {
    private val ctx: Context
        get() = MarimoCareApplication.instance

    private fun createSharedPreferences(): SharedPreferences {
        return ctx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, Context.MODE_PRIVATE)
    }

    private val sharedPreferences by lazy { createSharedPreferences() }

    var showAlertOverdue: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_ALERT_OVERDUE, true)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.SHOW_ALERT_OVERDUE, value) }

    var showAlertSoon: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_ALERT_SOON, true)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.SHOW_ALERT_SOON, value) }

    var alertOverdue: String
        get() = sharedPreferences.getString(AppUtils.ALERT_OVERDUE, "") ?: ""
        set(value) = sharedPreferences.edit { putString(AppUtils.ALERT_OVERDUE, value) }

    var alertSoon: String
        get() = sharedPreferences.getString(AppUtils.ALERT_SOON, "") ?: ""
        set(value) = sharedPreferences.edit { putString(AppUtils.ALERT_SOON, value) }

    var coloredIsSelected: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.COLORED_IS_SELECTED, false)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.COLORED_IS_SELECTED, value) }

    var tipsAutoScrollSped: Int
        get() = sharedPreferences.getInt(AppUtils.TIPS_AUTO_SCROLL_SPEED, 15)
        set(value) = sharedPreferences.edit() { putInt(AppUtils.TIPS_AUTO_SCROLL_SPEED, value) }

    var marimoFilter: Int
        get() = sharedPreferences.getInt(AppUtils.MARIMO_FILTER_SELECTED, -1)
        set(value) = sharedPreferences.edit() { putInt(AppUtils.MARIMO_FILTER_SELECTED, value) }

    var marimoSorting: Int
        get() = sharedPreferences.getInt(AppUtils.MARIMO_SORTING_SELECTED, 0)
        set(value) = sharedPreferences.edit() { putInt(AppUtils.MARIMO_SORTING_SELECTED, value) }

    var statPeriod: Int
        get() = sharedPreferences.getInt(AppUtils.STAT_PERIOD_SELECTED, 0)
        set(value) = sharedPreferences.edit() { putInt(AppUtils.STAT_PERIOD_SELECTED, value) }

    var showFilterAndSort: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_FILTER_AND_SORT, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_FILTER_AND_SORT, value) }

    var showBallonFirstTime: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_MARIMO_BALLON, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_MARIMO_BALLON, value) }

    var showBallonDashboardFirstTime: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_MARIMO_DASHBOARD_BALLON, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_MARIMO_DASHBOARD_BALLON, value) }

    var showBallonFirstUpdate: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_NEW_MARIMO_BALLON, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_NEW_MARIMO_BALLON, value) }

    var alertOverdueCounter: Int
        get() = sharedPreferences.getInt(AppUtils.MARIMO_OVERDUE_COUNTER, 0)
        set(value) = sharedPreferences.edit() { putInt(AppUtils.MARIMO_OVERDUE_COUNTER, value) }

    var fixWaterChanges: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.FIX, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.FIX, value) }

    var tabSelected: Int
        get() = sharedPreferences.getInt(AppUtils.TAB_SELECTED, 0)
        set(value) = sharedPreferences.edit() { putInt(AppUtils.TAB_SELECTED, value) }

    var lastHealthExecutionDate: String?
        get() = sharedPreferences.getString(AppUtils.LAST_HEALTH_EXECUTION_DATE, null)
        set(value) = sharedPreferences.edit { putString(AppUtils.LAST_HEALTH_EXECUTION_DATE,
            value) }

    var showNewLogChangeWater: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_NEW_LOG_CHANGE_WATER, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_NEW_LOG_CHANGE_WATER, value) }

    var showBallonNewStats: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_BALLON_NEW_STATS, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_BALLON_NEW_STATS, value) }

    var showBallonFeedback: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_BALLON_FEEDBACK, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_BALLON_FEEDBACK, value)}

    var showAlertToday: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_ALERT_TODAY, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_ALERT_TODAY, value) }

    var alertToday: String
        get() = sharedPreferences.getString(AppUtils.ALERT_TODAY, "") ?: ""
        set(value) = sharedPreferences.edit { putString(AppUtils.ALERT_TODAY, value) }

    var lastDailyNotificationDate: String
        get() = sharedPreferences.getString(AppUtils.LAST_DAILY_NOTIFICATION_DATE, "") ?: ""
        set(value) = sharedPreferences.edit { putString(AppUtils.LAST_DAILY_NOTIFICATION_DATE, value) }

    var showNewSettingsBalloon: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_NEW_SETTINGS_BALLOON, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_NEW_SETTINGS_BALLOON, value) }

}