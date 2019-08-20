package io.github.ovso.yearprogress.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import io.github.ovso.yearprogress.R
import io.github.ovso.yearprogress.service.DayWidgetUpdateService
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import java.util.Calendar

/**
 * Implementation of App Widget functionality.
 */

const val ACTION_AUTO_UPDATE_WIDGET = "ACTION_AUTO_UPDATE_WIDGET"

class DayAppWidget : AppWidgetProvider() {
  var service: PendingIntent? = null
  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    // There may be multiple widgets active, so update all of them
/*
    for (appWidgetId in appWidgetIds) {
      println("OJH onUpdate = $appWidgetId")
      updateAppWidget(context, appWidgetManager, appWidgetId)
    }
    Toast.makeText(context, "onUpdate", Toast.LENGTH_LONG).show()
*/

    val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, DayWidgetUpdateService::class.java)

    if (service == null) {
      service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }
    manager.setRepeating(
      AlarmManager.ELAPSED_REALTIME,
      SystemClock.elapsedRealtime(),
      60000,
      service
    )
  }

  override fun onEnabled(context: Context) {
    super.onEnabled(context)
    // Enter relevant functionality for when the first widget is created
    println("OJH onEnabled")

/*
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    calendar.add(Calendar.SECOND, 1)
    alarmManager.setRepeating(
      AlarmManager.RTC,
      calendar.timeInMillis,
      60000,
      createClockTickIntent(context)
    )
*/

  }

  private fun createClockTickIntent(context: Context): PendingIntent {
    val intent = Intent(ACTION_AUTO_UPDATE_WIDGET)
    return PendingIntent.getBroadcast(context, 0, intent, 0)
  }

  override fun onDisabled(context: Context) {
    // Enter relevant functionality for when the last widget is disabled
    println("OJH onDisabled")
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    //alarmManager.cancel(createClockTickIntent(context))
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

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
    Timber.d("onReceive")
  }

}

// https://medium.com/android-bits/android-widgets-ad3d166458d3
