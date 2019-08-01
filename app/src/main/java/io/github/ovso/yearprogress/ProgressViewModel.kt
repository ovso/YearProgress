package io.github.ovso.yearprogress

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class ProgressViewModel(val context: Context, val position: Int) : ViewModel() {

  val progressObField = ObservableField<SpannableString>()
  private val formBefore = "▓"
  private val formAfter = "░"

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
  }

  private fun setupMonth() {
    println("setupMonth")
  }

  private fun setupYear() {
    val year = hereAndNow().year
    val endDate = "$year-12-31 23:59"
    val ldtEnd = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val endTime = ldtEnd.atZone(ZoneId.of(ZoneId.systemDefault().id))
    val dayOfYear = endTime.dayOfYear
    val percent = (hereAndNow().dayOfYear.toDouble() / dayOfYear.toDouble() * 100).toInt()
    println("percent = $percent")
    val detailPercent = (hereAndNow().dayOfYear.toDouble() / dayOfYear.toDouble() * 100).round0()
    println("detailPercent = $detailPercent")
    val cntBefore = ((percent * 15) / 100)
    val cntAfter = 15 - cntBefore;
    val cntTotal = cntBefore + cntAfter;
    val formStringBuilder = StringBuilder()
    val spanBefore = SpannableStringBuilder()
    for (i in 1..cntBefore) formStringBuilder.append(formBefore)
    for (i in 1..cntAfter) formStringBuilder.append(formAfter)
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
