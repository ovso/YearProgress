package io.github.ovso.yearprogress

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  val formBefore = "▓"
  val formAfter = "░"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    val navView: NavigationView = findViewById(R.id.nav_view)
    val toggle = ActionBarDrawerToggle(
      this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
    )
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()
    navView.setNavigationItemSelectedListener(this)

    replaceFragment()
    //test()
  }

  private fun replaceFragment() {
    supportFragmentManager.beginTransaction()
      .replace(
        R.id.framelayout_main_replace_container,
        ProgressFragment.newInstance(0),
        ProgressFragment::class.java.simpleName
      ).commitNowAllowingStateLoss()
  }

  private fun test() {
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
    println("cntBefore = $cntBefore")
    println("cntAfter = $cntAfter")
    println("cntTotal = $cntTotal")
  }

  fun now(): Instant {
    return Instant.now()
  }

  fun hereAndNow(): ZonedDateTime {
    return ZonedDateTime.ofInstant(now(), ZoneId.systemDefault())
  }

  override fun onBackPressed() {
    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    // Handle navigation view item clicks here.
    when (item.itemId) {
      R.id.nav_home -> {
        // Handle the camera action
      }
      R.id.nav_gallery -> {

      }
      R.id.nav_slideshow -> {

      }
      R.id.nav_tools -> {

      }
      R.id.nav_share -> {

      }
      R.id.nav_send -> {

      }
    }
    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    drawerLayout.closeDrawer(GravityCompat.START)
    return true
  }
}
