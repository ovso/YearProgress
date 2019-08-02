package io.github.ovso.yearprogress

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
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