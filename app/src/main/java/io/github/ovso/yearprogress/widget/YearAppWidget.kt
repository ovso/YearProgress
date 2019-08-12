package io.github.ovso.yearprogress.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import io.github.ovso.yearprogress.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

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
      views.setTextViewText(R.id.appwidget_text, widgetText)
      views.setProgressBar(R.id.appwidget_progress, 100, getYearPer(), false);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
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

