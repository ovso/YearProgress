package io.github.ovso.yearprogress.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import android.app.AlarmManager
import java.util.Calendar
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import io.github.ovso.yearprogress.R

/**
 * Implementation of App Widget functionality.
 */

const val CLOCK_WIDGET_UPDATE = "com.eightbitcloud.example.widget.8BITCLOCK_WIDGET_UPDATE"

class DayAppWidget : AppWidgetProvider() {

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
    Toast.makeText(context, "onUpdate", Toast.LENGTH_LONG).show()
  }

  override fun onEnabled(context: Context) {
    // Enter relevant functionality for when the first widget is created
    println("OJH onEnabled")
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val calendar = Calendar.getInstance()
    calendar.setTimeInMillis(System.currentTimeMillis())
    calendar.add(Calendar.MINUTE, 1)
    alarmManager.setRepeating(
      AlarmManager.RTC,
      calendar.getTimeInMillis(),
      60000,
      createClockTickIntent(context)
    )
  }

  private fun createClockTickIntent(context: Context): PendingIntent {
    val intent = Intent(CLOCK_WIDGET_UPDATE)

    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
  }

  override fun onDisabled(context: Context) {
    // Enter relevant functionality for when the last widget is disabled
    println("OJH onDisabled")
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(createClockTickIntent(context))
  }

  companion object {

    internal fun updateAppWidget(
      context: Context, appWidgetManager: AppWidgetManager,
      appWidgetId: Int
    ) {
      println("OJH updateAppWidget")
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
