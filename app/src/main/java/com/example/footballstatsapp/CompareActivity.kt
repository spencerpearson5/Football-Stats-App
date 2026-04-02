package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.footballstatsapp.datamodel.Quarterbacks
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

// This activity let the user compare the stats of different players
class CompareActivity : AppCompatActivity() {

    // ViewModel to get Player data
    private lateinit var viewModel: MainViewModel
    
    // UI elements for display.
    private lateinit var player1Selector: AutoCompleteTextView
    private lateinit var player2Selector: AutoCompleteTextView
    private lateinit var comparisonLayout: LinearLayout
    private lateinit var player1Name: TextView
    private lateinit var player2Name: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    // store current player selections.
    private var allPlayers: List<Quarterbacks> = emptyList()
    private var selectedPlayer1: Quarterbacks? = null
    private var selectedPlayer2: Quarterbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load the  comparison layout.
        setContentView(R.layout.activity_compare)

        // Initialize the ViewModel.
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Link UI to match the xml file.
        player1Selector = findViewById(R.id.player1Search)
        player2Selector = findViewById(R.id.player2Search)
        comparisonLayout = findViewById(R.id.comparisonLayout)
        player1Name = findViewById(R.id.player1Name)
        player2Name = findViewById(R.id.player2Name)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // have the search suggestions be the same width as the search bar.
        player1Selector.post { player1Selector.dropDownWidth = player1Selector.width }
        player2Selector.post { player2Selector.dropDownWidth = player2Selector.width }

        // autocomplete.
        lifecycleScope.launch {
            viewModel.players.collect { players ->
                allPlayers = players
                setupAutocomplete(allPlayers)
            }
        }

        // bottom navigation bar for tab switching.
        setupBottomNavigation()
    }

   // Configure the autocomplete of the search function
    private fun setupAutocomplete(players: List<Quarterbacks>) {
        // retrieve distinct, sorted player names.
        val playerNames = players.map { it.name }.distinct().sorted()

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_player_item,
            R.id.dropdownText,
            playerNames
        )
        
        // Configure both selectors to take 1 player.
        player1Selector.setAdapter(adapter)
        player1Selector.threshold = 1
        
        player2Selector.setAdapter(adapter)
        player2Selector.threshold = 1

        // Handle item clicks for the first player.
        player1Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer1 = allPlayers.find { it.name.equals(selectedName, ignoreCase = true) }
            updateComparison()
        }

        // Handle item clicks for the second player.
        player2Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer2 = allPlayers.find { it.name.equals(selectedName, ignoreCase = true) }
            updateComparison()
        }
    }

    // update comparison updates the ui with the appropriate data when a player is selected
    private fun updateComparison() {
        val p1 = selectedPlayer1
        val p2 = selectedPlayer2

        // Only show comparison once both fields are valid players.
        if (p1 != null && p2 != null) {
            comparisonLayout.visibility = View.VISIBLE
            player1Name.text = p1.name
            player2Name.text = p2.name

            // Update Passing Yards
            findViewById<TextView>(R.id.p1Yards).text = p1.passing_yards
            findViewById<TextView>(R.id.p2Yards).text = p2.passing_yards

            // Update Passing TDs
            findViewById<TextView>(R.id.p1TDs).text = p1.passing_touchdowns
            findViewById<TextView>(R.id.p2TDs).text = p2.passing_touchdowns

            // Update Completions
            findViewById<TextView>(R.id.p1Completions).text = p1.completions
            findViewById<TextView>(R.id.p2Completions).text = p2.completions

            // Update Attempts
            findViewById<TextView>(R.id.p1Attempts).text = p1.attempts
            findViewById<TextView>(R.id.p2Attempts).text = p2.attempts

            // Update Completion Percentage
            findViewById<TextView>(R.id.p1Percentage).text = p1.completion_percentage
            findViewById<TextView>(R.id.p2Percentage).text = p2.completion_percentage

            // Update Interceptions
            findViewById<TextView>(R.id.p1Interceptions).text = p1.interceptions
            findViewById<TextView>(R.id.p2Interceptions).text = p2.interceptions
        } else {
            // Keep comparison hidden if either player is not yet selected.
            comparisonLayout.visibility = View.GONE
        }
    }

   // handles the bottom navigation bar
    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_compare
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_players -> {
                    startActivity(Intent(this, PlayersActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_compare -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}