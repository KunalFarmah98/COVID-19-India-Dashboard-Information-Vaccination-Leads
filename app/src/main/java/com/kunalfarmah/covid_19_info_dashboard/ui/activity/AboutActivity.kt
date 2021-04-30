package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "About"
        binding = ActivityAboutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.aboutDeveloper.text = Html.fromHtml("<u>Click here to learn more about the developer</u>")
        binding.aboutDeveloper.setOnClickListener {
            var intent = Intent(ACTION_VIEW)
            intent.data = Uri.parse("https://kunal-farmah.jimdosite.com/")
            if (isChromeInstalled()) {
                intent.`package` = "com.android.chrome"
                startActivity(intent)
            } else {
                startActivity(intent)
            }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

