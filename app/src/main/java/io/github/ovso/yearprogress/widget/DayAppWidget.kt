package io.github.ovso.yearprogress.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import io.github.ovso.yearprogress.R
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import java.util.Calendar

/**
 * Implementation of App Widget functionality.
 */

const val ACTION_AUTO_UPDATE_WIDGET = "android.appwidget.action.APPWIDGET_UPDATE"
const val ACTION_REFRESH = "action_refresh"

class DayAppWidget : AppWidgetProvider() {
  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    // There may be multiple widgets active, so update all of them
    Timber.d("OJH onUpdate()")
    for (appWidgetId in appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId)
    }
  }

  override fun onEnabled(context: Context) {
    // Enter relevant functionality for when the first widget is created
    println("OJH onEnabled()")
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    alarmManager.setRepeating(
      AlarmManager.RTC,
      calendar.timeInMillis,
      600000,
      createPendingIntent(context)
    )
    super.onEnabled(context)
  }

  private fun createPendingIntent(context: Context): PendingIntent {
    val intent = Intent(ACTION_AUTO_UPDATE_WIDGET)
    return PendingIntent.getBroadcast(
      context,
      0,
      intent,
      PendingIntent.FLAG_CANCEL_CURRENT
    )
  }

  override fun onDisabled(context: Context) {
    // Enter relevant functionality for when the last widget is disabled
    println("OJH onDisabled()")
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(createPendingIntent(context))
  }

  companion object {

    internal fun updateAppWidget(
      context: Context, appWidgetManager: AppWidgetManager,
      appWidgetId: Int
    ) {
      println("OJH updateAppWidget()")
//      val widgetText = context.getString(R.string.appwidget_text)
      val widgetText = "${getDayPer()}%"
      // Construct the RemoteViews object
      val views = RemoteViews(context.packageName, R.layout.year_app_widget)

      val title = context.resources.getStringArray(R.array.fragment_titles)[2]
      views.setTextViewText(R.id.tv_widget_title, title);
      views.setTextViewText(R.id.tv_widget_percent, widgetText)
      views.setProgressBar(R.id.progress_widget, 100, getDayPer(), false);
      setClickViews(context, views);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setClickViews(context: Context, views: RemoteViews) {
      val intent = Intent(context, DayAppWidget::class.java).apply {
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
    intent?.action?.let {
      if (it == ACTION_AUTO_UPDATE_WIDGET) {
        Timber.d("OJH onReceive ACTION_AUTO_UPDATE_WIDGET")
        val manager = AppWidgetManager.getInstance(context)
        onUpdate(
          context!!,
          manager,
          manager.getAppWidgetIds(ComponentName(context, DayAppWidget::class.java))
        )
      } else if (it == ACTION_REFRESH) {
        Timber.d("OJH onReceive ACTION_REFRESH")
      }
    }
  }

}

// https://medium.com/android-bits/android-widgets-ad3d166458d3
