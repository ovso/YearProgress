package io.github.ovso.yearprogress.view.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import de.psdev.licensesdialog.LicensesDialog
import io.github.ovso.yearprogress.Ads
import io.github.ovso.yearprogress.R.*
import io.github.ovso.yearprogress.databinding.ActivityMainBinding
import io.github.ovso.yearprogress.exts.loadAdaptiveBanner
import io.github.ovso.yearprogress.exts.loadInterstitial
import io.github.ovso.yearprogress.widget.BottomNav
import io.github.ovso.yearprogress.widget.EXTRA_NAME_INDEX
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

  private lateinit var viewModel: MainViewModel
  private lateinit var interstitialAd: InterstitialAd

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentView =
      DataBindingUtil.setContentView<ActivityMainBinding>(
        this,
        layout.activity_main
      )
    viewModel = provideViewModel()
    contentView.viewModel = viewModel
    setupActionBar()
    setupDrawer()
    setupBottomNavView()
    setupNavigationView()
    setupAds()
  }

  private fun setupAds() {
    loadAdaptiveBanner(fl_ads_container, Ads.BANNER_UNIT_ID)
    interstitialAd = loadInterstitial(Ads.INTERSTITIAL_UNIT_ID)
  }

  private fun setupNavigationView() {
    nav_view.setNavigationItemSelectedListener {
      drawer_layout.closeDrawer(GravityCompat.START)
      when (it.itemId) {
        id.nav_opensource -> showLicenseDialog()
        id.nav_review -> navigateToReview()
        id.nav_share -> navigateToShare()
      }

      true
    }
  }

  private fun setupActionBar() {
    setSupportActionBar(toolbar)
    title = getString(string.main_title)
  }

  private fun setupDrawer() {
    val toggle = ActionBarDrawerToggle(
      this,
      drawer_layout,
      toolbar,
      string.navigation_drawer_open,
      string.navigation_drawer_close
    )
    drawer_layout.addDrawerListener(toggle)
    toggle.syncState()
  }

  private fun provideViewModel() = ViewModelProviders.of(this).get(MainViewModel::class.java)

  private fun setupBottomNavView() {
    bottomNavigationView.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        id.bottom_nav_year -> viewModel.navSelectLiveData.value = BottomNav.YEAR.index
        id.bottom_nav_month -> viewModel.navSelectLiveData.value = BottomNav.MONTH.index
        id.bottom_nav_day -> viewModel.navSelectLiveData.value = BottomNav.DAY.index
      }
      true
    }

    viewModel.navSelectLiveData.observe(this, Observer { position ->

      interstitialAd.adListener = object : AdListener() {
        override fun onAdClosed() {
          replaceFragment(position)
        }

        override fun onAdFailedToLoad(p0: Int) {
          super.onAdFailedToLoad(p0)
          replaceFragment(position)
        }
      }

      if (interstitialAd.isLoaded) {
        interstitialAd.show()
      } else {
        replaceFragment(position)
      }

    })

    val index = intent.getIntExtra(EXTRA_NAME_INDEX, 0)
    viewModel.navSelectLiveData.postValue(index)
    val menuItem = bottomNavigationView.menu[index]
    menuItem.isChecked = true
  }

  private fun replaceFragment(position: Int) {
    supportFragmentManager.beginTransaction()
      .replace(
        id.framelayout_main_replace_container,
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
      .setNotices(raw.notices)
      .build()
      .show()
  }
}
