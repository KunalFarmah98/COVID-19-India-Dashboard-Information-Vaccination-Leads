package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.Intent
import android.content.SharedPreferences
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
    private var sharedPreferences:SharedPreferences?=null


    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Dashboard"

        dashboardViewModel.getLatestData()
        dashboardViewModel.getHistoryData()

        sharedPreferences = getSharedPreferences(Constants.PREFS,MODE_PRIVATE)

        bottomNav = findViewById(R.id.bottom_nav)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_contacts
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        bottomNav?.setOnNavigationItemSelectedListener {
            if(it.itemId==R.id.nav_home){
                if(bottomNav?.selectedItemId != R.id.nav_home){
                    supportActionBar?.title = "Dashboard"
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,DashboardFragment()).commit()
                }
            }
            else if(it.itemId==R.id.nav_history){
                if(bottomNav?.selectedItemId == R.id.nav_home){
                    supportActionBar?.title = "History"
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,HistoryFragment()).commit()
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

        if(bottomNav?.selectedItemId!=R.id.nav_home) {
            bottomNav?.selectedItemId = R.id.nav_home
            supportActionBar?.title = "Dashboard"
            bottomNav?.menu?.findItem(R.id.nav_home)?.isChecked = true
        }
        else if(bottomNav?.selectedItemId==R.id.nav_home)
            finish()
        else
            super.onBackPressed()

    }

    fun goToStateHistory(date:String, total:String, recovered:String, deceased:String){
        sharedPreferences?.edit()?.putString(Constants.SELECTED_DATE,date)?.apply()
        dashboardViewModel.getHistoryByDate(date)
        sharedPreferences?.edit()?.putString(Constants.SELECTED_TOTAL,total)?.apply()
        sharedPreferences?.edit()?.putString(Constants.SELECTED_RECOVERED,recovered)?.apply()
        sharedPreferences?.edit()?.putString(Constants.SELECTED_DECEASED,deceased)?.apply()

        startActivity(Intent(this@MainActivity,HistoryActivity::class.java))
    }
}