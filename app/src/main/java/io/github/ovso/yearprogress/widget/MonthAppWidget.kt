package io.github.ovso.yearprogress.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.ovso.yearprogress.R
import io.github.ovso.yearprogress.R.array
import io.github.ovso.yearprogress.view.main.MainActivity
import io.github.ovso.yearprogress.view.main.PERIOD_PROGRESS
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

/**
 * Implementation of App Widget functionality.
 */
class MonthAppWidget : AppWidgetProvider() {
  private val compositeDisposable = CompositeDisposable()
  private val progressAtomic = AtomicInteger(getMonthPer())

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    // There may be multiple widgets active, so update all of them
    for (appWidgetId in appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId, progressAtomic.get())
    }
  }

  override fun onEnabled(context: Context) {
    // Enter relevant functionality for when the first widget is created
    Timber.d("OJH onEnabled")
  }

  override fun onDisabled(context: Context) {
    // Enter relevant functionality for when the last widget is disabled
    Timber.d("OJH onDisabled")
    clearDisposable()
  }

  private fun addDisposable(d: Disposable) {
    compositeDisposable.add(d)
  }

  private fun clearDisposable() {
    compositeDisposable.clear()
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
    Timber.d("OJH onReceive counter = ${progressAtomic.get()}")
    intent?.action?.let {
      if (it == ACTION_REFRESH) {
        Timber.d("OJH Year onReceive action_refresh")
        progressAtomic.set(0)
        val manager = AppWidgetManager.getInstance(context)
        val monthPer = getMonthPer()
        clearDisposable()
        addDisposable(
          Observable.interval(PERIOD_PROGRESS, MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
              if (progressAtomic.get() > monthPer) {
                progressAtomic.set(0)
                clearDisposable()
              } else {
                onUpdate(
                  context!!,
                  manager,
                  manager.getAppWidgetIds(ComponentName(context, MonthAppWidget::class.java))
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
            putExtra(EXTRA_NAME_INDEX, BottomNav.MONTH.index)
          }
        )
      }
    }
  }

  companion object {

    internal fun updateAppWidget(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetId: Int,
      progress: Int
    ) {
      // Timber.d("updateAppWidget updateAppWidget progress = $progress")
      val widgetText = "$progress%"
      // Construct the RemoteViews object
      val views = RemoteViews(context.packageName, R.layout.year_app_widget)
      val title = context.resources.getStringArray(array.fragment_titles)[1]
      views.setTextViewText(R.id.tv_widget_title, title)
      views.setTextViewText(R.id.tv_widget_percent, widgetText)
      views.setProgressBar(R.id.progress_widget, 100, progress, false)
      setClickViews(context, views)
      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setClickViews(context: Context, views: RemoteViews) {
      views.setOnClickPendingIntent(
        R.id.ib_widget_refresh,
        PendingIntent.getBroadcast(
          context,
          0,
          Intent(context, MonthAppWidget::class.java).apply {
            action = ACTION_REFRESH
          },
          PendingIntent.FLAG_UPDATE_CURRENT
        )
      )

      views.setOnClickPendingIntent(
        R.id.fl_widget_root,
        PendingIntent.getBroadcast(
          context,
          1,
          Intent(context, MonthAppWidget::class.java).apply {
            action = ACTION_LAUNCHER_MAIN
          },
          PendingIntent.FLAG_UPDATE_CURRENT
        )
      )
    }

    private fun getMonthPer(): Int {
      val nowDay = hereAndNow().dayOfMonth
      val lastDay = hereAndNow().month.maxLength()
      return (nowDay.toDouble() / lastDay.toDouble() * 100).toInt()
    }

    private fun hereAndNow(): ZonedDateTime {
      return ZonedDateTime.ofInstant(now(), ZoneId.systemDefault())
    }

    private fun now(): Instant {
      return Instant.now()
    }
  }
}
