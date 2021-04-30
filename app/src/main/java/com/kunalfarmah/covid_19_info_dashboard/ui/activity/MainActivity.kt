package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.ui.WebViewFragment
import com.kunalfarmah.covid_19_info_dashboard.ui.dashboard.DashboardFragment
import com.kunalfarmah.covid_19_info_dashboard.ui.dashboard.DashboardViewModel
import com.kunalfarmah.covid_19_info_dashboard.ui.history.HistoryFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var bottomNav: BottomNavigationView? = null
    private var sharedPreferences: SharedPreferences? = null
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Dashboard"
        toolbar.title = "Dashboard"

        dashboardViewModel.getLatestData()
        dashboardViewModel.getHistoryData()
        dashboardViewModel.fetchContacts()

        sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)

        bottomNav = findViewById(R.id.bottom_nav)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_contacts
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        bottomNav?.setOnNavigationItemSelectedListener {
            if (it.itemId == R.id.nav_home) {
                if (bottomNav?.selectedItemId != R.id.nav_home) {
                    supportActionBar?.title = "Dashboard"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, DashboardFragment()).commit()
                }
            } else if (it.itemId == R.id.nav_history) {
                if (bottomNav?.selectedItemId == R.id.nav_home) {
                    supportActionBar?.title = "History"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HistoryFragment()).commit()
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {

        if (bottomNav?.selectedItemId != R.id.nav_home) {
            bottomNav?.selectedItemId = R.id.nav_home
            supportActionBar?.title = "Dashboard"
            bottomNav?.menu?.findItem(R.id.nav_home)?.isChecked = true
        } else if (bottomNav?.selectedItemId == R.id.nav_home) {
            finish()
        } else
            super.onBackPressed()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intent = Intent(this, WebViewActivity::class.java)
        when (item.itemId) {

            R.id.action_info -> {
                intent.putExtra("action", "Info")
                startActivity(intent)
            }
            R.id.action_donate -> {
                intent.putExtra("action", "Donate")
                startActivity(intent)
            }
            R.id.action_privacy -> {
                /* intent.putExtra("action","Privacy")
                 startActivity(intent)*/
                var intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://kunal-farmah.jimdosite.com/contact-me/")
                if (isChromeInstalled()) {
                    intent.`package` = "com.android.chrome"
                    startActivity(intent)
                } else {
                    startActivity(intent)
                }
            }
            R.id.action_about -> {
                intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }


    fun goToStateHistory(date: String, total: String, recovered: String, deceased: String) {
        sharedPreferences?.edit()?.putString(Constants.SELECTED_DATE, date)?.apply()
        dashboardViewModel.getHistoryByDate(date)
        sharedPreferences?.edit()?.putString(Constants.SELECTED_TOTAL, total)?.apply()
        sharedPreferences?.edit()?.putString(Constants.SELECTED_RECOVERED, recovered)?.apply()
        sharedPreferences?.edit()?.putString(Constants.SELECTED_DECEASED, deceased)?.apply()

        startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
    }

    fun goToDashboard(item: MenuItem) {
        drawerLayout.closeDrawers()
        if (bottomNav?.selectedItemId == R.id.nav_history) {
            bottomNav?.selectedItemId = R.id.nav_home
            supportActionBar?.title = "Dashboard"
            bottomNav?.menu?.findItem(R.id.nav_home)?.isChecked = true
        }
    }

    private fun isChromeInstalled(): Boolean {
        var pInfo: PackageInfo? = null
        pInfo = try {
            packageManager.getPackageInfo("com.android.chrome", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            //chrome is not installed on the device
            return false
        }
        return null != pInfo
    }

}