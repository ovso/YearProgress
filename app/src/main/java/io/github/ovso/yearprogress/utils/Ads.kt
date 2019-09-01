package io.github.ovso.yearprogress.utils

import io.github.ovso.yearprogress.BuildConfig

enum class Ads(val value: String) {
  ADMOB_APP_ID(
    if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544~3347511713" else "ca-app-pub-8679189423397017~1319798877"
  ),
  ADMOB_BANNER_UNIT_ID(
    if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/6300978111" else "ca-app-pub-8679189423397017/6496297456"
  ),
  ADMOB_INTERSTITIAL_ID(
    if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else "ca-app-pub-8679189423397017/7091000305"
  )
}