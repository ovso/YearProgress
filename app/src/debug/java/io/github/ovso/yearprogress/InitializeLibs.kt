package io.github.ovso.yearprogress

import timber.log.Timber

object InitializeLibs {

  fun timber() {
    Timber.plant(Timber.DebugTree())
  }
}
