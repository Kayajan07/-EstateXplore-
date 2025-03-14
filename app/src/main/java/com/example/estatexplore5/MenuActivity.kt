package com.example.estatexplore5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.estatexplore5.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val toolbar = binding.toolbar // Toolbar'ı al

        setSupportActionBar(toolbar) // Toolbar'ı action bar olarak ayarla

        // Menü butonu için ActionBarDrawerToggle ekleyelim
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // İlk açılışta HomeFragment'i gösterelim
        openFragment(HomeFragment())

        // Menüdeki tıklamaları dinleyelim
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> openFragment(HomeFragment())
                R.id.nav_properties -> openFragment(PropertiesFragment())
                R.id.nav_favorites -> openFragment(FavoritesFragment())
                R.id.nav_settings -> openFragment(SettingsFragment())
            }
            drawerLayout.closeDrawer(GravityCompat.START) // Menü kapansın
            true
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
