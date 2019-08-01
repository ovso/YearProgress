package io.github.ovso.yearprogress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
  val navSelectLiveData = MutableLiveData<Int>()
}