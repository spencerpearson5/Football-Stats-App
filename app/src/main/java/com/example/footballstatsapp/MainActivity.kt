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
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * MainActivity serves as the home screen of the application.
 * It features a search bar for players, a featured players list, and bottom navigation.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: AutoCompleteTextView
    private lateinit var searchButton: Button
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var viewModel: MainViewModel
    private lateinit var featuredPlayerAdapter: FeaturedPlayerAdapter
    private var allPlayers: List<Player> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Map UI views.
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        // Ensure dropdown width matches the search bar.
        searchEditText.post { searchEditText.dropDownWidth = searchEditText.width }

        // Initialize ViewModel.
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Configure the horizontal RecyclerView for featured players.
        val featuredRecyclerView: RecyclerView = findViewById(R.id.featuredRecyclerView)
        featuredPlayerAdapter = FeaturedPlayerAdapter(emptyList()) { player ->
            navigateToProfile(player)
        }
        featuredRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        featuredRecyclerView.adapter = featuredPlayerAdapter

        // Observe player data from the ViewModel.
        lifecycleScope.launch {
            viewModel.players.collect { playerList ->
                allPlayers = playerList

                // Select two random players to feature on the home screen.
                if (allPlayers.isNotEmpty()) {
                    val featuredPlayers = allPlayers.shuffled().take(2)
                    featuredPlayerAdapter.updateData(featuredPlayers)
                }

                // Populate the autocomplete suggestions for the search bar.
                setupAutocomplete(allPlayers)
            }
        }

        // Handle item clicks from the search suggestions.
        searchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            val selectedPlayer = allPlayers.find { it.name.equals(selectedName, ignoreCase = true) }
            if (selectedPlayer != null) navigateToProfile(selectedPlayer)
        }

        // Handle the search button click.
        searchButton.setOnClickListener {
            val playerName = searchEditText.text.toString().trim()
            if (playerName.isEmpty()) {
                Toast.makeText(this, "Enter a player name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedPlayer = allPlayers.find { it.name.equals(playerName, ignoreCase = true) }
            if (selectedPlayer != null) {
                navigateToProfile(selectedPlayer)
            } else {
                Toast.makeText(this, "Player not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Configure bottom navigation transitions.
        setupBottomNavigation()
    }

    /**
     * Initializes the autocomplete dropdown with distinct player names.
     */
    private fun setupAutocomplete(players: List<Player>) {
        val playerNames = players.map { it.name }.distinct().sorted()
        val adapter = ArrayAdapter(this, R.layout.dropdown_player_item, R.id.dropdownText, playerNames)
        searchEditText.setAdapter(adapter)
        searchEditText.threshold = 1
    }

    /**
     * Navigates to the PlayerProfileActivity with the selected player's full statistics.
     */
    private fun navigateToProfile(player: Player) {
        val intent = Intent(this, PlayerProfileActivity::class.java).apply {
            putExtra("player_name", player.name)
            putExtra("team", player.team)
            putExtra("passing_yards", player.passingYards.toInt().toString())
            putExtra("passing_touchdowns", player.passingTouchdowns.toInt().toString())
            putExtra("completions", player.passingCompletions.toInt().toString())
            putExtra("attempts", player.passingAttempts.toInt().toString())
            putExtra("completion_percentage", player.completionPercentage.toString())
            putExtra("interceptions", player.passingInterceptions.toInt().toString())
        }
        startActivity(intent)
    }

    /**
     * Sets up listeners for the BottomNavigationView.
     */
    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_leaderboards -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    true
                }
                R.id.nav_players -> {
                    startActivity(Intent(this, PlayersActivity::class.java))
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
