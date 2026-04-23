package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Player
import com.example.footballstatsapp.datamodel.PlayerProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var chartAdapter: LeaderboardChartAdapter
    private lateinit var bottomNavigation: BottomNavigationView

    private val statCategories = listOf(
        "Yards",
        "TDs",
        "Completions",
        "Attempts",
        "Percentage",
        "Ints"
    )

    private val years = (2025 downTo 2004).map { it.toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val chartRecyclerView: RecyclerView = findViewById(R.id.chartRecyclerView)
        chartAdapter = LeaderboardChartAdapter(emptyList(), "Yards")
        chartRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        chartRecyclerView.adapter = chartAdapter

        val recyclerView: RecyclerView = findViewById(R.id.leaderboardRecyclerView)
        playerAdapter = PlayerAdapter(emptyList()) { player ->
            val intent = Intent(this, PlayerProfileActivity::class.java)
            intent.putExtra("player_name", player.name)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playerAdapter

        setupSpinners()
        setupBottomNavigation()

        lifecycleScope.launch {
            viewModel.players.collect {
                refreshData()
            }
        }
    }

    private fun setupSpinners() {
        val statSpinner: Spinner = findViewById(R.id.statSpinner)
        val yearSpinner: Spinner = findViewById(R.id.yearSpinner)

        statSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            statCategories
        )

        yearSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            years
        )

        // Set default selection to 2025
        val defaultYearIndex = years.indexOf("2025")
        if (defaultYearIndex != -1) {
            yearSpinner.setSelection(defaultYearIndex)
        }

        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                refreshData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        statSpinner.onItemSelectedListener = itemSelectedListener
        yearSpinner.onItemSelectedListener = itemSelectedListener
    }

    private fun refreshData() {
        val statSpinner: Spinner = findViewById(R.id.statSpinner)
        val yearSpinner: Spinner = findViewById(R.id.yearSpinner)

        val selectedStat = statSpinner.selectedItem?.toString() ?: "Yards"
        val selectedYear = yearSpinner.selectedItem?.toString()?.toIntOrNull() ?: 2025

        updateLeaderboard(selectedStat, selectedYear)
    }


    private fun updateLeaderboard(category: String, year: Int) {
        val allProfiles = viewModel.players.value

        val seasonRowsForYear: List<Player> = allProfiles.mapNotNull { profile ->
            profile.seasons.find { it.season == year }
        }

        // displaying stats for selected year
        val sortedSeasonRows = when (category) {
            "Yards" -> seasonRowsForYear.sortedByDescending { it.passingYards }
            "TDs" -> seasonRowsForYear.sortedByDescending { it.passingTouchdowns }
            "Percentage" -> seasonRowsForYear.sortedByDescending {
                it.completionPercentage
            }
            "Ints" -> seasonRowsForYear.sortedByDescending{ it.passingInterceptions } 
            "Completions" -> seasonRowsForYear.sortedByDescending {
                it.passingCompletions
            }
            "Attempts" -> seasonRowsForYear.sortedByDescending {
                it.passingAttempts
            }
            else -> seasonRowsForYear
        }


        // fixing previous error of every player saying one season, now says correct number
        val leaderboardProfiles: List<PlayerProfile> = sortedSeasonRows.mapNotNull { player ->
            allProfiles.find { it.name == player.name }?.let { originalProfile ->
                // overriding to show the stats of the selected season instead of the players
                // most recent season
                originalProfile.copy(seasons = originalProfile.seasons).apply {
                    displaySeasonOverride = player
                }
            }
        }

        playerAdapter.update_data(leaderboardProfiles, category)
        chartAdapter.updateData(sortedSeasonRows.take(15), category)
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_leaderboards
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
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
                R.id.nav_leaderboards -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
