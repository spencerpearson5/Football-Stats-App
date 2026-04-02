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
import com.example.footballstatsapp.datamodel.Quarterbacks
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var chartAdapter: LeaderboardChartAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private val statCategories = listOf("Yards", "TDs", "Completions", "Attempts", "Percentage", "Ints")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val chartRecyclerView: RecyclerView = findViewById(R.id.chartRecyclerView)
        chartAdapter = LeaderboardChartAdapter(emptyList(), "Yards")
        chartRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        chartRecyclerView.adapter = chartAdapter

        val recyclerView: RecyclerView = findViewById(R.id.leaderboardRecyclerView)
        playerAdapter = PlayerAdapter(emptyList()) { /* Click logic */ }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playerAdapter

        setupRecyclerView()
        setupSpinner()
        setupBottomNavigation()

        lifecycleScope.launch {
            viewModel.players.collect {
                val currentStat = findViewById<Spinner>(R.id.statSpinner).selectedItem.toString()
                updateLeaderboard(currentStat)
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
                putExtra("passing_yards", player.passing_yards)
                putExtra("passing_touchdowns", player.passing_touchdowns)
                putExtra("completions", player.completions)
                putExtra("attempts", player.attempts)
                putExtra("completion_percentage", player.completion_percentage)
                putExtra("interceptions", player.interceptions)
            }
            startActivity(intent)
        }
        recyclerView.adapter = playerAdapter
    }

    private fun setupSpinner() {
        val spinner: Spinner = findViewById(R.id.statSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statCategories)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateLeaderboard(statCategories[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateLeaderboard(category: String) {
        val allPlayers = viewModel.players.value

        val sortedList = when (category) {
            "Yards" -> allPlayers.sortedByDescending { it.yardsInt }
            "TDs" -> allPlayers.sortedByDescending { it.tdInt }
            "Percentage" -> allPlayers.sortedByDescending { it.percentageFloat }
            "Ints" -> allPlayers.sortedByDescending { it.intInt }
            "Completions" -> allPlayers.sortedByDescending { it.completionsInt }
            "Attempts" -> allPlayers.sortedByDescending { it.attemptsInt }
            else -> allPlayers
        }

        playerAdapter.update_data(sortedList, category)

        chartAdapter.updateData(sortedList.take(15), category)
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