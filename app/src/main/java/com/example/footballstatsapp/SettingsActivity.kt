package com.example.footballstatsapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        
        //  switch the state
        darkModeSwitch.isChecked = isDarkMode

        // set listener for switching between themes
        darkModeSwitch.setOnClickListener {
            val isChecked = darkModeSwitch.isChecked
            val currentPref = sharedPreferences.getBoolean("dark_mode", false)
            
            if (isChecked != currentPref) {
                sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()

                val mode = if (isChecked) {AppCompatDelegate.MODE_NIGHT_YES } else { AppCompatDelegate.MODE_NIGHT_NO }
                
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_settings
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); true }
                R.id.nav_players -> { startActivity(Intent(this, PlayersActivity::class.java)); true }
                R.id.nav_leaderboards -> { startActivity(Intent(this, LeaderboardActivity::class.java)); true }
                R.id.nav_compare -> { startActivity(Intent(this, CompareActivity::class.java)); true }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
