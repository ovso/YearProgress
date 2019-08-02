package io.github.ovso.yearprogress

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger

class ProgressViewModel(val context: Context, val position: Int) : ViewModel() {

  val progressObField = ObservableField<Int>()
  val percentObField = ObservableField<String>()
  val atomicInt = AtomicInteger(-1)
  fun getTitle(): String = context.resources.getStringArray(R.array.fragment_titles)[position]

  init {
    when (position) {
      0 -> setupPercent(getYearPer())
      1 -> setupPercent(getMonthPer())
      2 -> setupPercent(getDayPer())
    }
  }

  private var subscribe: Disposable? = null
  private fun setupPercent(percent: Int) {
    println("percent = $percent")
    progressObField.set(atomicInt.getAndIncrement())
    percentObField.set("${atomicInt.getAndIncrement()}%")
    subscribe = Observable.interval(25, MILLISECONDS)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe {
        if (atomicInt.get() > percent) {
          subscribe?.dispose()
        } else {
          progressObField.set(atomicInt.getAndIncrement())
          percentObField.set("${atomicInt.getAndIncrement()}%")
        }
      }
  }

  private fun getDayPer() =
    (hereAndNow().toLocalTime().hour.toDouble() / 24.toDouble() * 100).toInt()

  private fun getMonthPer(): Int {
    val nowDay = hereAndNow().dayOfMonth
    val lastDay = hereAndNow().month.maxLength()
    return (nowDay.toDouble() / lastDay.toDouble() * 100).toInt()
  }

  private fun getYearPer(): Int {
    val year = hereAndNow().year
    val endDate = "$year-12-31 23:59"
    val ldtEnd = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val endTime = ldtEnd.atZone(ZoneId.of(ZoneId.systemDefault().id))
    val nowDayOfYear = hereAndNow().dayOfYear
    val endDayOfYear = endTime.dayOfYear
    return (nowDayOfYear.toDouble() / endDayOfYear.toDouble() * 100).toInt()
  }

  private fun now(): Instant {
    return Instant.now()
  }

  private fun hereAndNow(): ZonedDateTime {
    return ZonedDateTime.ofInstant(now(), ZoneId.systemDefault())
  }

  override fun onCleared() {
    super.onCleared()
    subscribe?.dispose()
  }
}
