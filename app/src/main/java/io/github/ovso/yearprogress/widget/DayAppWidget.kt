package io.github.ovso.yearprogress.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import android.app.AlarmManager
import android.app.AlertDialog
import java.util.Calendar
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast
import io.github.ovso.yearprogress.R
import timber.log.Timber

/**
 * Implementation of App Widget functionality.
 */

const val ACTION_AUTO_UPDATE_WIDGET = "ACTION_AUTO_UPDATE_WIDGET"

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
    super.onEnabled(context)
    // Enter relevant functionality for when the first widget is created
    println("OJH onEnabled")
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.timeInMillis = System.currentTimeMillis()
    calendar.add(Calendar.SECOND, 1)
    alarmManager.setRepeating(
      AlarmManager.RTC,
      calendar.timeInMillis,
      AlarmManager.INTERVAL_HALF_HOUR,
      createClockTickIntent(context)
    )
  }

  private fun createClockTickIntent(context: Context): PendingIntent {
    val intent = Intent(ACTION_AUTO_UPDATE_WIDGET)
    return PendingIntent.getBroadcast(context, 0, intent, 0)
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

//https://stackoverflow.com/questions/5476867/updating-app-widget-using-alarmmanager
//http://allandroidprojects.blogspot.com/2016/06/android-widget-tutorial-updating-with.html
//https://github.com/brucejcooper/Android-Examples/blob/master/WidgetExample/src/com/eightbitcloud/example/widget/ExampleAppWidgetProvider.java
//https://stackoverflow.com/questions/15391334/change-widget-update-interval-programatically-android
//https://code.tutsplus.com/tutorials/code-a-widget-for-your-android-app-updating-your-widget--cms-30528