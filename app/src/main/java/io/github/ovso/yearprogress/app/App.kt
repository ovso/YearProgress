package io.github.ovso.yearprogress.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.ovso.yearprogress.BuildConfig
import io.github.ovso.yearprogress.utils.Ads
import timber.log.Timber.DebugTree
import timber.log.Timber

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    AndroidThreeTen.init(this)
    if (BuildConfig.DEBUG) {
      Timber.plant(DebugTree())
    }
    MobileAds.initialize(this, Ads.ADMOB_APP_ID.value)
  }
}