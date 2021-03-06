package io.github.ovso.yearprogress.exts

import android.app.Activity
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

private fun Activity.adaptiveBannerAdSize(): AdSize {
  val display = windowManager.defaultDisplay
  val outMetrics = DisplayMetrics()
  display.getMetrics(outMetrics)

  val density = outMetrics.density

  var adWidthPixels = 0f
  if (adWidthPixels == 0f) {
    adWidthPixels = outMetrics.widthPixels.toFloat()
  }

  val adWidth = (adWidthPixels / density).toInt()
  return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}

private fun Fragment.adaptiveBannerAdSize(): AdSize {
  val context = requireActivity()
  val display = context.windowManager.defaultDisplay
  val outMetrics = DisplayMetrics()
  display.getMetrics(outMetrics)

  val density = outMetrics.density

  var adWidthPixels = 0f
  if (adWidthPixels == 0f) {
    adWidthPixels = outMetrics.widthPixels.toFloat()
  }

  val adWidth = (adWidthPixels / density).toInt()
  return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
}

fun Activity.loadAdaptiveBanner(container: ViewGroup, unitId: String) {
  val adView = AdView(container.context)
  container.addView(adView)

  fun load() {
    adView.adUnitId = unitId
    adView.adSize = adaptiveBannerAdSize()
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
  }

  load()
}


fun ViewGroup.loadBanner(unitId: String) {
  val adView = AdView(this.context)
  this.addView(adView)

  fun load() {
    adView.adUnitId = unitId
    adView.adSize = AdSize.BANNER
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
  }

  load()
}

fun Fragment.loadAdaptiveBanner(container: ViewGroup, unitId: String) {
  val adView = AdView(container.context)
  container.addView(adView)

  fun load() {
    adView.adUnitId = unitId
    adView.adSize = adaptiveBannerAdSize()
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
  }

  load()
}

fun Activity.loadInterstitial(unitId: String): InterstitialAd {
  return InterstitialAd(applicationContext).apply {
    adUnitId = unitId
    loadAd(AdRequest.Builder().build())
  }
}
