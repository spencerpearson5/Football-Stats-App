package com.example.footballstatsapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//Chris Imports
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
<<<<<<< Updated upstream
import kotlinx.coroutines.launch


=======
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

>>>>>>> Stashed changes
class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
<<<<<<< Updated upstream
    private lateinit var favoritesButton: Button
    private lateinit var recentButton: Button
    private lateinit var browseTeamsButton: Button

    private lateinit var viewModel: MainViewModel
    private lateinit var player_adapter: PlayerAdapter
=======
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var viewModel: MainViewModel
    private lateinit var featuredPlayerAdapter: FeaturedPlayerAdapter
    private var allPlayers: List<Player> = emptyList()
>>>>>>> Stashed changes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

<<<<<<< Updated upstream
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val recycler_view: RecyclerView = findViewById(R.id.recyclerView)
        player_adapter = PlayerAdapter(emptyList())
        recycler_view.adapter = player_adapter
        viewModel.load_stats()

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        favoritesButton = findViewById(R.id.favoritesButton)
        recentButton = findViewById(R.id.recentButton)
        browseTeamsButton = findViewById(R.id.browseTeamsButton)

        searchButton.setOnClickListener {
            val playerName = searchEditText.text.toString().trim()

            if (playerName.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter a player name",
                    Toast.LENGTH_SHORT
                ).show()
=======
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        val featuredRecyclerView: RecyclerView = findViewById(R.id.featuredRecyclerView)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        featuredPlayerAdapter = FeaturedPlayerAdapter(emptyList()) { player ->
            navigateToProfile(player)
        }
        featuredRecyclerView.layoutManager = LinearLayoutManager(this)
        featuredRecyclerView.adapter = featuredPlayerAdapter

        lifecycleScope.launch {
            viewModel.players.collect { players ->
                allPlayers = players

                val topPerformers = players
                    .sortedByDescending { it.passingTouchdowns }
                    .take(15)
                featuredPlayerAdapter.updateData(topPerformers)

                val playerNames = players.map { it.name }.distinct().sorted()
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, playerNames)
                searchEditText.setAdapter(adapter)

                featuredPlayerAdapter.updateData(players.sortedByDescending { it.passingTouchdowns }.take(10))
            }
        }

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            val player = allPlayers.find { it.name.equals(query, ignoreCase = true) }
            if (player != null) {
                navigateToProfile(player)
>>>>>>> Stashed changes
            } else {
                Toast.makeText(
                    this,
                    "Searching for $playerName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

<<<<<<< Updated upstream
        lifecycleScope.launch {
            viewModel.players.collect { player_list ->
                player_adapter.update_data(player_list)
=======
        setupBottomNavigation()
    }

    private fun navigateToProfile(player: Player) {
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

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_leaderboards -> { startActivity(Intent(this, LeaderboardActivity::class.java)); true }
                R.id.nav_players -> { startActivity(Intent(this, PlayersActivity::class.java)); true }
                R.id.nav_compare -> { startActivity(Intent(this, CompareActivity::class.java)); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
                else -> false
>>>>>>> Stashed changes
            }
        }

        favoritesButton.setOnClickListener {
            Toast.makeText(
                this,
                "Leaderboards page coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        recentButton.setOnClickListener {
            Toast.makeText(
                this,
                "Player Profiles page coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        browseTeamsButton.setOnClickListener {
            Toast.makeText(
                this,
                "Player Comparisons page coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}