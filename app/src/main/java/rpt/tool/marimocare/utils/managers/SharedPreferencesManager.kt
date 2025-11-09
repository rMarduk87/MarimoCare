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

    var alerts: String?
        get() = sharedPreferences.getString(AppUtils.ALERTS, "")
        set(value) = sharedPreferences.edit() { putString(AppUtils.ALERTS, value) }

    var showAlert: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_ALERT, true)
        set(value) = sharedPreferences.edit() { putBoolean(AppUtils.SHOW_ALERT, value) }

}