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
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

//this activity let the user compare the stats of different players
class CompareActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    //ui elements
    private lateinit var player1Selector: AutoCompleteTextView
    private lateinit var player2Selector: AutoCompleteTextView
    private lateinit var comparisonLayout: LinearLayout
    private lateinit var player1Name: TextView
    private lateinit var player2Name: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    //store current player selections.
    private var allPlayers: List<Player> = emptyList()
    private var selectedPlayer1: Player? = null
    private var selectedPlayer2: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //load the  comparison
        setContentView(R.layout.activity_compare)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        player1Selector = findViewById(R.id.player1Search)
        player2Selector = findViewById(R.id.player2Search)
        comparisonLayout = findViewById(R.id.comparisonLayout)
        player1Name = findViewById(R.id.player1Name)
        player2Name = findViewById(R.id.player2Name)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        //have the search suggestions be the same width as the search bar.
        player1Selector.post { player1Selector.dropDownWidth = player1Selector.width }
        player2Selector.post { player2Selector.dropDownWidth = player2Selector.width }

        lifecycleScope.launch {
            viewModel.players.collect { players ->
                allPlayers = players
                setupAutocomplete(allPlayers)
            }
        }

        //bottom navigation bar for tab switching.
        setupBottomNavigation()
    }

    private fun setupAutocomplete(players: List<Player>) {
        val playerNames = players.map { it.name }.distinct().sorted()

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_player_item,
            R.id.dropdownText,
            playerNames
        )

        player1Selector.setAdapter(adapter)
        player1Selector.threshold = 1

        player2Selector.setAdapter(adapter)
        player2Selector.threshold = 1

        player1Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer1 = allPlayers.find { it.name.equals(selectedName, ignoreCase = true) }
            updateComparison()
        }

        player2Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer2 = allPlayers.find { it.name.equals(selectedName, ignoreCase = true) }
            updateComparison()
        }
    }

    //update comparison updates the ui with data when a player selected
    private fun updateComparison() {
        val p1 = selectedPlayer1
        val p2 = selectedPlayer2

        if (p1 != null && p2 != null) {
            comparisonLayout.visibility = View.VISIBLE

            // Use .Name (Capital N) to match your Player model
            player1Name.text = p1.name
            player2Name.text = p2.name

            // Helper to turn 4000.0 into "4000"
            fun cleanNum(d: Double): String = d.toInt().toString()

            findViewById<TextView>(R.id.p1Yards).text = cleanNum(p1.passingYards)
            findViewById<TextView>(R.id.p2Yards).text = cleanNum(p2.passingYards)

            findViewById<TextView>(R.id.p1TDs).text = cleanNum(p1.passingTouchdowns)
            findViewById<TextView>(R.id.p2TDs).text = cleanNum(p2.passingTouchdowns)

            findViewById<TextView>(R.id.p1Completions).text = cleanNum(p1.passingCompletions)
            findViewById<TextView>(R.id.p2Completions).text = cleanNum(p2.passingCompletions)

            findViewById<TextView>(R.id.p1Attempts).text = cleanNum(p1.passingAttempts)
            findViewById<TextView>(R.id.p2Attempts).text = cleanNum(p2.passingAttempts)

            //format percentage to one decimal
            findViewById<TextView>(R.id.p1Percentage).text = String.format("%.1f%%", p1.completionPercentage)
            findViewById<TextView>(R.id.p2Percentage).text = String.format("%.1f%%", p2.completionPercentage)

            findViewById<TextView>(R.id.p1Interceptions).text = cleanNum(p1.passingInterceptions)
            findViewById<TextView>(R.id.p2Interceptions).text = cleanNum(p2.passingInterceptions)
        } else {
            comparisonLayout.visibility = View.GONE
        }
    }

    //handle the bottom navigation bar
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
                R.id.nav_leaderboards -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
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