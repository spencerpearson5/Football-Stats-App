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
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var favoritesButton: Button
    private lateinit var recentButton: Button
    private lateinit var browseTeamsButton: Button

    private lateinit var viewModel: MainViewModel
    private lateinit var player_adapter: PlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            } else {
                Toast.makeText(
                    this,
                    "Searching for $playerName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launch {
            viewModel.players.collect { player_list ->
                player_adapter.update_data(player_list)
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