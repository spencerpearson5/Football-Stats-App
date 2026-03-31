package com.example.footballstatsapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Quarterbacks
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
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

        lifecycleScope.launch {
            viewModel.players.collect { playerList ->
                allPlayers = playerList
                filterPlayers(searchEditText.text.toString())
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                filterPlayers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

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
}