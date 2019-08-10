package io.github.ovso.yearprogress

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.psdev.licensesdialog.LicensesDialog
import io.github.ovso.yearprogress.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import kotlinx.android.synthetic.main.content_main.bottomNavigationView
import timber.log.Timber

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
    setupNavigationView()
  }

  private fun setupNavigationView() {
    nav_view.setNavigationItemSelectedListener {
      drawer_layout.closeDrawer(GravityCompat.START)
      when (it.itemId) {
        R.id.nav_opensource -> showLicenseDialog()
        R.id.nav_review -> navigateToReview()
        R.id.nav_share -> navigateToShare()
      }

      true
    }
  }

  private fun setupActionBar() {
    setSupportActionBar(toolbar)
    title = getString(R.string.main_title)
  }

  private fun setupDrawer() {
    val toggle = ActionBarDrawerToggle(
      this,
      drawer_layout,
      toolbar,
      R.string.navigation_drawer_open,
      R.string.navigation_drawer_close
    )
    drawer_layout.addDrawerListener(toggle)
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
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  private fun navigateToShare() {
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      addCategory(Intent.CATEGORY_DEFAULT)
      putExtra(Intent.EXTRA_TITLE, "Share")
      putExtra(Intent.EXTRA_TEXT, "market://details?value=$packageName")
    }
    startActivity(Intent.createChooser(intent, "App share"))
  }

  private fun navigateToReview() {
    val intent = Intent(Intent.ACTION_VIEW).apply {
      val uriString = "https://play.google.com/store/apps/details?id=$packageName"
      data = Uri.parse(uriString)
      setPackage("com.android.vending")
    }
    try {
      startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      Timber.e(e)
    }

  }

  private fun showLicenseDialog() {
    LicensesDialog.Builder(this)
      .setNotices(R.raw.notices)
      .build()
      .show()
  }
}
