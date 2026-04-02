package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Quarterbacks
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: AutoCompleteTextView
    private lateinit var searchButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var viewModel: MainViewModel
    private lateinit var featuredPlayerAdapter: FeaturedPlayerAdapter

    private var allPlayers: List<Quarterbacks> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ThemeUtils.applyTheme(this)

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val featuredRecyclerView: RecyclerView =
            findViewById(R.id.featuredRecyclerView)

        featuredPlayerAdapter = FeaturedPlayerAdapter(emptyList()) { player ->
            openPlayerProfile(player)
        }

        featuredRecyclerView.adapter = featuredPlayerAdapter
        featuredRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        searchEditText.post {
            searchEditText.dropDownWidth = searchEditText.width
        }

        lifecycleScope.launch {
            viewModel.players.collect { playerList ->
                allPlayers = playerList

                val featuredPlayers = allPlayers
                    .shuffled()
                    .take(2)

                featuredPlayerAdapter.updateData(featuredPlayers)

                setupAutocomplete(allPlayers)
            }
        }

        searchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()

            val selectedPlayer = allPlayers.find { player ->
                player.name.equals(selectedName, ignoreCase = true)
            }

            if (selectedPlayer != null) {
                openPlayerProfile(selectedPlayer)
            } else {
                Toast.makeText(
                    this,
                    "Player not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        searchButton.setOnClickListener {
            val playerName = searchEditText.text.toString().trim()

            if (playerName.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter a player name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val selectedPlayer = allPlayers.find { player ->
                player.name.equals(playerName, ignoreCase = true)
            }

            if (selectedPlayer != null) {
                openPlayerProfile(selectedPlayer)
            } else {
                Toast.makeText(
                    this,
                    "Player not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_home

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_leaderboards -> {
                    Toast.makeText(
                        this,
                        "Leaderboards page coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.nav_players -> {
                    startActivity(Intent(this, PlayersActivity::class.java))
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

    private fun setupAutocomplete(players: List<Quarterbacks>) {
        val playerNames = players.map { it.name }.distinct().sorted()

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_player_item,
            R.id.dropdownText,
            playerNames
        )

        searchEditText.setAdapter(adapter)
        searchEditText.threshold = 1
    }

    private fun openPlayerProfile(player: Quarterbacks) {
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
}