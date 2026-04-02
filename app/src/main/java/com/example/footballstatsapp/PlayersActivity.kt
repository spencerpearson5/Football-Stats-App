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

class PlayersActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ThemeUtils.applyTheme(this)

        setContentView(R.layout.activity_players)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)

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

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playerAdapter

        lifecycleScope.launch {
            viewModel.players.collect { players ->
                playerAdapter.update_data(players)
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_players

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }

                @Suppress("ControlFlowWithEmptyBody")
                R.id.nav_players -> {
                    true
                }

                R.id.nav_leaderboards -> {
                    Toast.makeText(
                        this,
                        "Leaderboards coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.nav_compare -> {
                    Toast.makeText(
                        this,
                        "Compare page coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
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