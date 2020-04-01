package io.github.ovso.yearprogress.view.main

import android.content.Context
import android.graphics.Color
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import io.github.ovso.yearprogress.R.array
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

const val PERIOD_PROGRESS = 25L

class ProgressViewModel(val context: Context, private val position: Int) : ViewModel() {

  val progressObField = ObservableField<Int>()
  val percentObField = ObservableField<String>()
  val percentColorObField = ObservableField<Int>()
  private val atomicInt = AtomicInteger(0)
  private var intervalDisposable: Disposable? = null

  fun getTitle(): String = context.resources.getStringArray(array.fragment_titles)[position]

  init {

    when (position) {
      0 -> setupPercent(getYearPer())
      1 -> setupPercent(getMonthPer())
      2 -> setupPercent(getDayPer())
    }
  }

  private fun setupPercent(percent: Int) {
    Timber.d("setupPercent($percent)")
    progressObField.set(atomicInt.get())
    percentObField.set("${atomicInt.get()}%")
    intervalDisposable = Observable.interval(PERIOD_PROGRESS, MILLISECONDS)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe {
        if (atomicInt.get() > percent) {
          intervalDisposable?.dispose()
          atomicInt.set(0)
        } else {
          progressObField.set(atomicInt.get())
          percentObField.set("${atomicInt.get()}%")
          val color =
            Color.parseColor(context.resources.getStringArray(array.percent_colors2)[atomicInt.get() / 10])
          percentColorObField.set(color)

          atomicInt.incrementAndGet()
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
    intervalDisposable?.dispose()
  }
}
