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

/**
 * SettingsActivity allows users to manage app preferences, such as Dark Mode,
 * and perform administrative actions like refreshing player statistics in the database.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var refreshStatsButton: Button
    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply the saved theme preference globally before loading the layout.
        
        setContentView(R.layout.activity_settings)

        // Initialize SharedPreferences to store and retrieve user settings.
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        
        // Bind UI components to their layout IDs.
        refreshStatsButton = findViewById(R.id.refreshStatsButton)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Set the switch state based on the current saved preference.
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode

        // Action: Scrapes latest data and updates the Firestore collection.
        refreshStatsButton.setOnClickListener {
            lifecycleScope.launch {
                refreshStatsButton.isEnabled = false
                refreshStatsButton.text = "Refreshing..."
                try {
                    // Trigger the repository to refresh data from the web.
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

        // Action: Toggles between Light and Dark themes.
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the new preference.
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            
            // Apply the theme change immediately.
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Configure navigation through the bottom bar.
        setupBottomNavigation()
    }

    /**
     * Sets up the listener for the BottomNavigationView and manages activity switching.
     */
    private fun setupBottomNavigation() {
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
                R.id.nav_leaderboards -> {
                    Toast.makeText(this, "Leaderboards coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_compare -> {
                    // Navigate to the completed CompareActivity.
                    startActivity(Intent(this, CompareActivity::class.java))
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}