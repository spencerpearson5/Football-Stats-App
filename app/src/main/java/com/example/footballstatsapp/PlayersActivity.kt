package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * PlayersActivity displays a scrollable list of all players fetched from the database.
 */
class PlayersActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply the saved theme preference before the view is created.
        
        setContentView(R.layout.activity_players)

        // Initialize the ViewModel to access player data.
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Link UI components.
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Initialize the adapter with an empty list and a click listener for player profiles.
        playerAdapter = PlayerAdapter(emptyList()) { player ->
            val intent = Intent(this, PlayerProfileActivity::class.java)
            intent.putExtra("player_name", player.name)
            intent.putExtra("team", player.team)
            intent.putExtra("passing_yards", player.passing_yards)
            intent.putExtra("passing_touchdowns", player.passing_touchdowns)
            intent.putExtra("completions", player.completions)
            intent.putExtra("attempts", player.attempts)
            intent.putExtra("completion_percentage", player.completion_percentage)
            intent.putExtra("interceptions", player.interceptions)
            startActivity(intent)
        }

        // Set up the RecyclerView with a vertical layout manager.
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playerAdapter

        // Collect player data from the ViewModel and update the adapter.
        lifecycleScope.launch {
            viewModel.players.collect { players ->
                playerAdapter.update_data(players)
            }
        }

        // Configure the bottom navigation bar.
        setupBottomNavigation()
    }

    /**
     * Sets up the listener for BottomNavigationView and handles activity transitions.
     */
    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_players
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_players -> true
                R.id.nav_leaderboards -> {
                    Toast.makeText(this, "Leaderboards coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_compare -> {
                    // Launch the completed CompareActivity.
                    startActivity(Intent(this, CompareActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}