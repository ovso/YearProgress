package io.github.ovso.yearprogress

import android.content.Context
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

const val FORM_BEFORE = "▓"
const val FORM_AFTER = "░"
const val FORM_MAX_COUNT = 15

class ProgressViewModel(val context: Context, val position: Int) : ViewModel() {

  val progressObField = ObservableField<SpannableString>()

  fun getTitle(): String = context.resources.getStringArray(R.array.fragment_titles)[position]

  init {
    when (position) {
      0 -> setupYear()
      1 -> setupMonth()
      2 -> setupDay()
    }
  }

  private fun setupDay() {
    println("setupDay")
    val percent = (hereAndNow().toLocalTime().hour.toDouble() / 24.toDouble() * 100).toInt()
    println("setupDay percent = $percent")
    val cntBefore = (percent * FORM_MAX_COUNT) / 100
    val cntAfter = FORM_MAX_COUNT - cntBefore
    val formStringBuilder = StringBuilder()
    for (i in 1..cntBefore) formStringBuilder.append(FORM_BEFORE)
    for (i in 1..cntAfter) formStringBuilder.append(FORM_AFTER)
    formStringBuilder.append("  $percent%")
    val span = SpannableString(formStringBuilder.toString())
    span.setSpan(RelativeSizeSpan(01.4f), 0, cntBefore, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    progressObField.set(span)
  }

  private fun setupMonth() {
    println("setupMonth")
    val nowDay = hereAndNow().dayOfMonth
    val lastDay = hereAndNow().month.maxLength()
    val percent = (nowDay.toDouble() / lastDay.toDouble() * 100).toInt()
    val cntBefore = (percent * FORM_MAX_COUNT) / 100
    val cntAfter = FORM_MAX_COUNT - cntBefore
    val formStringBuilder = StringBuilder()
    for (i in 1..cntBefore) formStringBuilder.append(FORM_BEFORE)
    for (i in 1..cntAfter) formStringBuilder.append(FORM_AFTER)
    formStringBuilder.append("  $percent%")
    val span = SpannableString(formStringBuilder.toString())
    span.setSpan(RelativeSizeSpan(01.4f), 0, cntBefore, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    progressObField.set(span)
  }

  private fun setupYear() {
    val year = hereAndNow().year
    val endDate = "$year-12-31 23:59"
    val ldtEnd = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val endTime = ldtEnd.atZone(ZoneId.of(ZoneId.systemDefault().id))
    val nowDayOfYear = hereAndNow().dayOfYear
    val endDayOfYear = endTime.dayOfYear
    val percent = (nowDayOfYear.toDouble() / endDayOfYear.toDouble() * 100).toInt()
    println("percent = $percent")
    val cntBefore = ((percent * FORM_MAX_COUNT) / 100)
    val cntAfter = FORM_MAX_COUNT - cntBefore;
    val cntTotal = cntBefore + cntAfter;
    val formStringBuilder = StringBuilder()
    for (i in 1..cntBefore) formStringBuilder.append(FORM_BEFORE)
    for (i in 1..cntAfter) formStringBuilder.append(FORM_AFTER)
    formStringBuilder.append("  $percent%")
    val span = SpannableString(formStringBuilder.toString())
    span.setSpan(RelativeSizeSpan(01.4f), 0, cntBefore, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    progressObField.set(span)
    println("cntBefore = $cntBefore")
    println("cntAfter = $cntAfter")
    println("cntTotal = $cntTotal")
  }

  private fun now(): Instant {
    return Instant.now()
  }

  private fun hereAndNow(): ZonedDateTime {
    return ZonedDateTime.ofInstant(now(), ZoneId.systemDefault())
  }

}
