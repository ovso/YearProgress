package io.github.ovso.yearprogress.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import io.github.ovso.yearprogress.R
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class DayWidgetUpdateService : Service() {
  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    updateAppWidget(this, AppWidgetManager.getInstance(this), 0);
    return super.onStartCommand(intent, flags, startId)
  }

  companion object {

    internal fun updateAppWidget(
      context: Context, appWidgetManager: AppWidgetManager,
      appWidgetId: Int
    ) {
      println("OJH DayWidgetUpdateService updateAppWidget")
//      val widgetText = context.getString(R.string.appwidget_text)
      val widgetText = "${getDayPer()}%"
      // Construct the RemoteViews object
      val views = RemoteViews(context.packageName, R.layout.year_app_widget)

      val title = context.resources.getStringArray(R.array.fragment_titles)[2]
      views.setTextViewText(R.id.tv_widget_title, title);
      views.setTextViewText(R.id.tv_widget_percent, widgetText)
      views.setProgressBar(R.id.progress_widget, 100, getDayPer(), false);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getDayPer() =
      (hereAndNow().toLocalTime().hour.toDouble() / 24.toDouble() * 100).toInt()

    private fun hereAndNow(): ZonedDateTime {
      return ZonedDateTime.ofInstant(now(), ZoneId.systemDefault())
    }

    private fun now(): Instant {
      return Instant.now()
    }
  }

}