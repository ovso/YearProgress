package io.github.ovso.yearprogress.app

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.ovso.yearprogress.BuildConfig
import timber.log.Timber.DebugTree
import timber.log.Timber



class App: Application() {

  override fun onCreate() {
    super.onCreate()
    AndroidThreeTen.init(this)
    if (BuildConfig.DEBUG) {
      Timber.plant(DebugTree())
    }
  }
}