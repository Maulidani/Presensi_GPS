package com.skripsi.presensigps.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.skripsi.presensigps.R
import com.skripsi.presensigps.adapter.ViewPagerAdapter
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private val vAdapter = ViewPagerAdapter(this.supportFragmentManager, this.lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        sharedPref = PreferencesHelper(this)
        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewPager.adapter = vAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Presensi"
                1 -> tab.text = "Laporan"
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemProfile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
            R.id.itemAbout -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            finish()
        }
    }

}