package com.example.footballstatsapp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Entry point of the application.
 * This class is used for global configuration, specifically managing the visual theme.
 */
class FootballStatsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Apply the saved theme preference as soon as the app process starts.
        applyTheme(this)
    }

    companion object {
        /**
         * Reads the saved 'dark_mode' preference from SharedPreferences
         * and applies the corresponding Night Mode setting globally.
         * 
         * @param context The application context to access SharedPreferences.
         */
        fun applyTheme(context: Context) {
            val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}
