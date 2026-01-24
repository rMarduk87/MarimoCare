package rpt.tool.marimocare.utils.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import rpt.tool.marimocare.MainActivity
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager

class MarimoAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    companion object {

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_update_marimo)

            val count = getMarimoCount(context)

            views.setTextViewText(R.id.badge, count.toString())
            views.setViewVisibility(
                R.id.badge,
                if (count > 0) View.VISIBLE else View.GONE
            )

            // Click → apre app
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("OPEN_UPDATE", true)

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.icon, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }

        private fun getMarimoCount(context: Context): Int {
            return SharedPreferencesManager.marimoUpdateCount
        }
    }
}
