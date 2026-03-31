package com.example.footballstatsapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Quarterbacks
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: AutoCompleteTextView
    private lateinit var searchButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter

    private var allPlayers: List<Quarterbacks> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        playerAdapter = PlayerAdapter(emptyList())
        recyclerView.adapter = playerAdapter

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        searchEditText.post {
            searchEditText.dropDownWidth = searchEditText.width
        }


        lifecycleScope.launch {
            viewModel.players.collect { playerList ->
                allPlayers = playerList
                playerAdapter.update_data(allPlayers)
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
                playerAdapter.update_data(allPlayers)
                Toast.makeText(
                    this,
                    "Showing all players",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                filterPlayers(playerName)
                Toast.makeText(
                    this,
                    "Searching for $playerName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_home

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }

                R.id.nav_leaderboards -> {
                    Toast.makeText(
                        this,
                        "Leaderboards page coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.nav_players -> {
                    Toast.makeText(
                        this,
                        "Players page coming soon",
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

    private fun filterPlayers(query: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            playerAdapter.update_data(allPlayers)
            return
        }

        val filteredPlayers = allPlayers.filter { player ->
            player.name.contains(trimmedQuery, ignoreCase = true)
        }

        playerAdapter.update_data(filteredPlayers)
    }

    private fun openPlayerProfile(player: Quarterbacks) {
        val intent = Intent(this, PlayerProfileActivity::class.java)

        intent.putExtra("player_name", player.name)
        intent.putExtra("team", player.team)
        intent.putExtra("passing_yards", player.passing_yards)
        intent.putExtra(
            "passing_touchdowns",
            player.passing_touchdowns
        )

        startActivity(intent)
    }
}