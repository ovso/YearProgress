package io.github.ovso.yearprogress.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.ovso.yearprogress.Ads
import io.github.ovso.yearprogress.InitializeLibs

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    InitializeLibs.timber()
    AndroidThreeTen.init(this)
    MobileAds.initialize(this, Ads.APP_ID)
  }
}
