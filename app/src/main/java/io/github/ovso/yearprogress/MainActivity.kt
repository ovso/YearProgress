package io.github.ovso.yearprogress

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.ovso.yearprogress.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import kotlinx.android.synthetic.main.content_main.bottomNavigationView

class MainActivity : AppCompatActivity() {

  private lateinit var viewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val contentView =
      DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    viewModel = provideViewModel()
    contentView.viewModel = viewModel
    setupActionBar()
    setupDrawer()
    setupBottonNavView()
  }

  private fun setupActionBar() {
    setSupportActionBar(toolbar)
    title = getString(R.string.main_title)
  }

  private fun setupDrawer() {
    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    val toggle = ActionBarDrawerToggle(
      this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
    )
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()
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

}
