package io.github.ovso.yearprogress.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import android.widget.Toast
import io.github.ovso.yearprogress.R
import timber.log.Timber

class MyWidgetProvider : AppWidgetProvider() {
  override fun onReceive(
    context: Context?,
    intent: Intent?
  ) {
    super.onReceive(context, intent)
    Timber.d("onReceive")
    intent?.let {
      if (it.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
        it.extras?.let {
          val appWidgetIds = it.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
          if (appWidgetIds != null && appWidgetIds.size > 0) {
            this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
          }
        }
      } else {
        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun onUpdate(
    context: Context?,
    appWidgetManager: AppWidgetManager?,
    appWidgetIds: IntArray?
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    appWidgetIds?.forEach {
      Timber.d("it = $it")
      context?.let { ctx ->
        val remoteViews = RemoteViews(ctx.packageName, R.layout.widget_year)
        appWidgetManager?.updateAppWidget(it, remoteViews)
      }
    }
    Timber.d("onUpdate")
  }

}