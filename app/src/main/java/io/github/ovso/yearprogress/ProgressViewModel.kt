package io.github.ovso.yearprogress

import android.content.Context
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask

const val FORM_BEFORE = "▓"
const val FORM_AFTER = "░"
const val FORM_MAX_COUNT = 15

class ProgressViewModel(val context: Context, val position: Int) : ViewModel() {

  val progressObField = ObservableField<SpannableString>()
  val progressBarObField = ObservableField<Int>()
  val percentObField = ObservableField<String>()
  fun getTitle(): String = context.resources.getStringArray(R.array.fragment_titles)[position]

  init {
    when (position) {
      0 -> setupPercent(getYearPer())
      1 -> setupPercent(getMonthPer())
      2 -> setupPercent(getDayPer())
    }
  }

  private fun setupPercent(percent: Int) {
    val cntBefore = (percent * FORM_MAX_COUNT) / 100
    val cntAfter = FORM_MAX_COUNT - cntBefore
    val formStringBuilder = StringBuilder()
    for (i in 1..cntBefore) formStringBuilder.append(FORM_BEFORE)
    for (i in 1..cntAfter) formStringBuilder.append(FORM_AFTER)
    formStringBuilder.append("  $percent%")
    val span = SpannableString(formStringBuilder.toString())
    span.setSpan(RelativeSizeSpan(1f), 0, cntBefore, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    progressObField.set(span)
    progressBarObField.set(percent)
    percentObField.set("$percent%")
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

}
