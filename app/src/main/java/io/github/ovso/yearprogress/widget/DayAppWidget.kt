package io.github.ovso.yearprogress.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.ovso.yearprogress.R
import io.github.ovso.yearprogress.view.main.MainActivity
import io.github.ovso.yearprogress.view.main.PERIOD_PROGRESS
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of App Widget functionality.
 */

class DayAppWidget : AppWidgetProvider() {

  private val compositeDisposable = CompositeDisposable()
  private val progressAtomic = AtomicInteger(getDayPer())

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    // There may be multiple widgets active, so update all of them
    Timber.d("OJH onUpdate()")
    for (appWidgetId in appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId, progressAtomic.get())
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
    clearDisposable()
  }

  companion object {

    internal fun updateAppWidget(
      context: Context, appWidgetManager: AppWidgetManager,
      appWidgetId: Int,
      progress:Int
    ) {
      Timber.d("updateAppWidget updateAppWidget progress = $progress")
//      val widgetText = context.getString(R.string.appwidget_text)
      val widgetText = "$progress%"
      // Construct the RemoteViews object
      val views = RemoteViews(context.packageName, R.layout.year_app_widget)

      val title = context.resources.getStringArray(R.array.fragment_titles)[2]
      views.setTextViewText(R.id.tv_widget_title, title);
      views.setTextViewText(R.id.tv_widget_percent, widgetText)
      views.setProgressBar(R.id.progress_widget, 100, progress, false)
      setClickViews(context, views)

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

      views.setOnClickPendingIntent(
        R.id.fl_widget_root,
        PendingIntent.getBroadcast(
          context,
          1,
          Intent(context, DayAppWidget::class.java).apply {
            action = ACTION_LAUNCHER_MAIN
          },
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

  private fun addDisposable(d: Disposable) {
    compositeDisposable.add(d)
  }

  private fun clearDisposable() {
    compositeDisposable.clear()
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
      } else       if (it == ACTION_REFRESH) {
        Timber.d("OJH Year onReceive action_refresh")
        progressAtomic.set(0)
        val manager = AppWidgetManager.getInstance(context)
        val dayPer = getDayPer()
        clearDisposable()
        addDisposable(
          Observable.interval(PERIOD_PROGRESS, MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
              if (progressAtomic.get() > dayPer) {
                progressAtomic.set(0)
                clearDisposable()
              } else {
                onUpdate(
                  context!!,
                  manager,
                  manager.getAppWidgetIds(ComponentName(context, DayAppWidget::class.java))
                )
                progressAtomic.incrementAndGet()
              }
            }
        )

      } else if (it == ACTION_LAUNCHER_MAIN) {
        context!!.startActivity(
          Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_NAME_INDEX, BottomNav.DAY.index)
          }
        )
      }

    }
  }

}

// https://medium.com/android-bits/android-widgets-ad3d166458d3
