package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch


class PlayersActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        playerAdapter = PlayerAdapter(emptyList()) { player ->
            val intent = Intent(this, PlayerProfileActivity::class.java).apply {
                putExtra("player_name", player.name)
                putExtra("team", player.team)
                putExtra("passing_yards", player.passingYards)
                putExtra("passing_touchdowns", player.passingTouchdowns)
                putExtra("completions", player.passingCompletions)
                putExtra("attempts", player.passingAttempts)
                putExtra("completion_percentage", player.completionPercentage)
                putExtra("interceptions", player.passingInterceptions)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playerAdapter

        //update the list
        lifecycleScope.launch {
            viewModel.players.collect { players ->
                val uniquePlayers = players
                    .sortedByDescending { it.season }
                    .distinctBy { it.name }

                playerAdapter.update_data(uniquePlayers)
            }
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_players
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    true
                }
                R.id.nav_players -> true
                R.id.nav_leaderboards -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    true
                }
                R.id.nav_compare -> {
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