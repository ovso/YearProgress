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
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import io.github.ovso.yearprogress.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import kotlinx.android.synthetic.main.content_main.bottomNavigationView
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  private lateinit var viewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val contentView =
      DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    viewModel = provideViewModel()
    contentView.viewModel = viewModel
    setSupportActionBar(toolbar)

    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    val navView: NavigationView = findViewById(R.id.nav_view)
    val toggle = ActionBarDrawerToggle(
      this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
    )
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()
    navView.setNavigationItemSelectedListener(this)

    setupBottonNavView()
  }

  private fun provideViewModel() = ViewModelProviders.of(this).get(MainViewModel::class.java)

  private fun setupBottonNavView() {
    bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.bottom_nav_year -> viewModel.navSelectLiveData.value = 0
        R.id.bottom_nav_month -> viewModel.navSelectLiveData.value = 1
        R.id.bottom_nav_day -> viewModel.navSelectLiveData.value = 2
      }
      true
    }

    viewModel.navSelectLiveData.observe(this, Observer {
      replaceFragment(it)
    })
    viewModel.navSelectLiveData.postValue(0)

  }

  private fun replaceFragment(position: Int) {
    supportFragmentManager.beginTransaction()
      .replace(
        R.id.framelayout_main_replace_container,
        ProgressFragment.newInstance(position),
        ProgressFragment::class.java.simpleName
      ).commitNowAllowingStateLoss()
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
