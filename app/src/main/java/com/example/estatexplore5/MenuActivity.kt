package com.example.estatexplore5

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.estatexplore5.databinding.ActivityMenuBinding
import com.example.estatexplore5.ui.*
import com.google.android.material.navigation.NavigationView

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // ✅ GİRİŞ YAPANIN ROLE'ÜNÜ AL
        userRole = intent.getStringExtra("role")

        // ✅ MENÜDE ROL'E GÖRE GÖSTER/GİZLE
        setupNavigationMenu()

        // Default fragment açılışta
        openFragment(HomeFragment())
        supportActionBar?.title = "Home" // ✅ Başlığı ilk açılışta da ayarla

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    openFragment(HomeFragment())
                    supportActionBar?.title = "Home"
                }
                R.id.nav_properties -> {
                    openFragment(PropertiesFragment())
                    supportActionBar?.title = "Properties"
                }
                R.id.nav_favorites -> {
                    openFragment(FavoritesFragment())
                    supportActionBar?.title = "Favorites"
                }
                R.id.nav_settings -> {
                    openFragment(SettingsFragment())
                    supportActionBar?.title = "Settings"
                }
                R.id.nav_admin_panel -> {
                    openFragment(AdminPanelFragment())
                    supportActionBar?.title = "Admin Panel"
                }
                R.id.nav_my_listings -> {
                    openFragment(AdminPanelFragment()) // Eğer MyListingsFragment varsa onu kullanabilirsin
                    supportActionBar?.title = "My Listings"
                }
                R.id.navigation_add_property -> {
                    openFragment(AddPropertyFragment())
                    supportActionBar?.title = "Add Property"
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupNavigationMenu() {
        val menu = navView.menu

        if (userRole == "admin") {
            // Admin ise sadece Admin Panel menüsü açık
            menu.findItem(R.id.nav_admin_panel).isVisible = true
            menu.findItem(R.id.nav_my_listings).isVisible = false
        } else {
            // Normal kullanıcı ise sadece My Listings menüsü açık
            menu.findItem(R.id.nav_admin_panel).isVisible = false
            menu.findItem(R.id.nav_my_listings).isVisible = true
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}




