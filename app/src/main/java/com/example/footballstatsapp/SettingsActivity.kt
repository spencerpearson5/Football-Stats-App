package com.example.footballstatsapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.example.footballstatsapp.data.PlayerRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var refreshStatsButton: Button
    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        
        refreshStatsButton = findViewById(R.id.refreshStatsButton)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Set initial state of switch
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode

        refreshStatsButton.setOnClickListener {
            lifecycleScope.launch {
                refreshStatsButton.isEnabled = false
                refreshStatsButton.text = "Refreshing..."
                try {
                    PlayerRepository.refresh_stats_in_firestore()
                    Toast.makeText(this@SettingsActivity, "Stats refreshed!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@SettingsActivity, "Refresh failed", Toast.LENGTH_SHORT).show()
                } finally {
                    refreshStatsButton.isEnabled = true
                    refreshStatsButton.text = "Refresh Stats"
                }
            }
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_settings
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_players -> {
                    startActivity(Intent(this, PlayersActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> {
                    Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }
}