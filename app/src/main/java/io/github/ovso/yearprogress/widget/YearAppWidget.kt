package io.github.ovso.yearprogress.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import io.github.ovso.yearprogress.R
import io.github.ovso.yearprogress.R.array
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

/**
 * Implementation of App Widget functionality.
 */
class YearAppWidget : AppWidgetProvider() {

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    // There may be multiple widgets active, so update all of them
    for (appWidgetId in appWidgetIds) {
      println("OJH onUpdate = $appWidgetId")
      updateAppWidget(context, appWidgetManager, appWidgetId)
    }
  }

  override fun onEnabled(context: Context) {
    // Enter relevant functionality for when the first widget is created
    println("OJH onEnabled")
  }

  override fun onDisabled(context: Context) {
    // Enter relevant functionality for when the last widget is disabled
    println("OJH onDisabled")
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
    intent?.action?.let {
      if (it == ACTION_REFRESH) {
        Timber.d("OJH Year onReceive action_refresh")
        val manager = AppWidgetManager.getInstance(context)
        onUpdate(
          context!!,
          manager,
          manager.getAppWidgetIds(ComponentName(context, YearAppWidget::class.java))
        )

        Toast.makeText(context, R.string.widget_msg_updated, Toast.LENGTH_SHORT).show()
      }
    }

  }

  companion object {

    internal fun updateAppWidget(
      context: Context, appWidgetManager: AppWidgetManager,
      appWidgetId: Int
    ) {
      println("OJH updateAppWidget")
//      val widgetText = context.getString(R.string.appwidget_text)
      val widgetText = "${getYearPer()}%"
      // Construct the RemoteViews object
      val views = RemoteViews(context.packageName, R.layout.year_app_widget)
      val title = context.resources.getStringArray(array.fragment_titles)[0]
      views.setTextViewText(R.id.tv_widget_title, title);
      views.setTextViewText(R.id.tv_widget_percent, widgetText)
      views.setProgressBar(R.id.progress_widget, 100, getYearPer(), false);
      setClickViews(context, views);
      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setClickViews(context: Context, views: RemoteViews) {
      val intent = Intent(context, YearAppWidget::class.java).apply {
        action = ACTION_REFRESH
      }
      views.setOnClickPendingIntent(
        R.id.ib_widget_refresh,
        PendingIntent.getBroadcast(
          context,
          0,
          intent,
          PendingIntent.FLAG_UPDATE_CURRENT
        )
      )
    }

    private fun getYearPer(): Int {
      val year = hereAndNow().year
      val endDate = "$year-12-31 23:59"
      val ldtEnd = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
      val endTime = ldtEnd.atZone(ZoneId.of(ZoneId.systemDefault().id))
      val nowDayOfYear = hereAndNow().dayOfYear
      val endDayOfYear = endTime.dayOfYear
      return (nowDayOfYear.toDouble() / endDayOfYear.toDouble() * 100).toInt()
    }

    private fun hereAndNow(): ZonedDateTime {
      return ZonedDateTime.ofInstant(now(), ZoneId.systemDefault())
    }

    private fun now(): Instant {
      return Instant.now()
    }
  }
}
