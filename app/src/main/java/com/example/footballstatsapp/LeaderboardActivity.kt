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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var chartAdapter: LeaderboardChartAdapter
    private lateinit var bottomNavigation: BottomNavigationView

    private val statCategories = listOf("Yards", "TDs", "Completions", "Attempts", "Percentage", "Ints")

    // set years
    private val years = (2024 downTo 2005).map { it.toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // chart setup
        val chartRecyclerView: RecyclerView = findViewById(R.id.chartRecyclerView)
        chartAdapter = LeaderboardChartAdapter(emptyList(), "Yards")
        chartRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        chartRecyclerView.adapter = chartAdapter

        setupRecyclerView()
        setupSpinners()
        setupBottomNavigation()

        lifecycleScope.launch {
            viewModel.players.collect {
                refreshData()
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
        recyclerView.adapter = playerAdapter
    }

    private fun setupSpinners() {
        val statSpinner: Spinner = findViewById(R.id.statSpinner)
        val yearSpinner: Spinner = findViewById(R.id.yearSpinner)

        // for stat
        statSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statCategories)

        // for year
        yearSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)

        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                refreshData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        statSpinner.onItemSelectedListener = itemSelectedListener
        yearSpinner.onItemSelectedListener = itemSelectedListener
    }

    //get current filter and sorting selections
    private fun refreshData() {
        val selectedStat = findViewById<Spinner>(R.id.statSpinner).selectedItem.toString()
        val selectedYear = findViewById<Spinner>(R.id.yearSpinner).selectedItem.toString().toInt()

        updateLeaderboard(selectedStat, selectedYear)
    }

    private fun updateLeaderboard(category: String, year: Int) {
        val allPlayers = viewModel.players.value

        // filter by the selected year
        // sort by the selected stat category
        val filteredAndSortedList = allPlayers
            .filter { it.season == year }
            .let { list ->
                when (category) {
                    "Yards" -> list.sortedByDescending { it.passingYards }
                    "TDs" -> list.sortedByDescending { it.passingTouchdowns }
                    "Percentage" -> list.sortedByDescending { it.completionPercentage }
                    "Ints" -> list.sortedByDescending { it.passingInterceptions }
                    "Completions" -> list.sortedByDescending { it.passingCompletions }
                    "Attempts" -> list.sortedByDescending { it.passingAttempts }
                    else -> list
                }
            }

        playerAdapter.update_data(filteredAndSortedList, category)
        chartAdapter.updateData(filteredAndSortedList.take(10), category)
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_leaderboards
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); true }
                R.id.nav_players -> { startActivity(Intent(this, PlayersActivity::class.java)); true }
                R.id.nav_compare -> { startActivity(Intent(this, CompareActivity::class.java)); true }
                R.id.nav_leaderboards -> true
                else -> false
            }
        }
    }
}