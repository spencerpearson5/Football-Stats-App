package com.example.footballstatsapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.footballstatsapp.datamodel.Player
import com.example.footballstatsapp.datamodel.PlayerProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.*

class CompareActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var player1Selector: AutoCompleteTextView
    private lateinit var player2Selector: AutoCompleteTextView
    private lateinit var comparisonLayout: LinearLayout
    private lateinit var player1Image: ImageView
    private lateinit var player2Image: ImageView
    private lateinit var player1SeasonSpinner: Spinner
    private lateinit var player2SeasonSpinner: Spinner
    private lateinit var player1Name: TextView
    private lateinit var player2Name: TextView
    private lateinit var player1Team: TextView
    private lateinit var player2Team: TextView
    private lateinit var player1ColorBar: View
    private lateinit var player2ColorBar: View
    private lateinit var bottomNavigation: BottomNavigationView

    // cached stat views
    private lateinit var p1Stats: PlayerStatViews
    private lateinit var p2Stats: PlayerStatViews
    
    private var allPlayers: List<PlayerProfile> = emptyList()
    private var selectedPlayer1: PlayerProfile? = null
    private var selectedPlayer2: PlayerProfile? = null

    private var selectedSeason1: Player? = null
    private var selectedSeason2: Player? = null

    // player stat that will be displayed
    private data class PlayerStatViews(
        val yards: TextView,
        val tds: TextView,
        val completions: TextView,
        val attempts: TextView,
        val percentage: TextView,
        val interceptions: TextView
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // ui
        player1Selector = findViewById(R.id.player1Search)
        player2Selector = findViewById(R.id.player2Search)
        comparisonLayout = findViewById(R.id.comparisonLayout)
        player1Name = findViewById(R.id.player1Name)
        player2Name = findViewById(R.id.player2Name)
        player1Team = findViewById(R.id.player1Team)
        player2Team = findViewById(R.id.player2Team)
        player1ColorBar = findViewById(R.id.player1ColorBar)
        player2ColorBar = findViewById(R.id.player2ColorBar)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        player1Image = findViewById(R.id.player1Image)
        player2Image = findViewById(R.id.player2Image)
        player1SeasonSpinner = findViewById(R.id.player1SeasonSpinner)
        player2SeasonSpinner = findViewById(R.id.player2SeasonSpinner)

        // storing stats for each player  so that they can be applied more easily
        p1Stats = PlayerStatViews(
            findViewById(R.id.p1Yards), findViewById(R.id.p1TDs),
            findViewById(R.id.p1Completions), findViewById(R.id.p1Attempts),
            findViewById(R.id.p1Percentage), findViewById(R.id.p1Interceptions)
        )
        p2Stats = PlayerStatViews(
            findViewById(R.id.p2Yards), findViewById(R.id.p2TDs),
            findViewById(R.id.p2Completions), findViewById(R.id.p2Attempts),
            findViewById(R.id.p2Percentage), findViewById(R.id.p2Interceptions)
        )
        
        player1Selector.post { player1Selector.dropDownWidth = player1Selector.width }
        player2Selector.post { player2Selector.dropDownWidth = player2Selector.width }

        lifecycleScope.launch {
            viewModel.players.collect { players ->
                allPlayers = players
                setupAutocomplete(allPlayers)
            }
        }

        setupBottomNavigation()
    }

    // autocomplete for search bars
    private fun setupAutocomplete(players: List<PlayerProfile>) {
        val playerNames = players.map { it.name }.distinct().sorted()
        val adapter = ArrayAdapter(this, R.layout.dropdown_player_item, R.id.dropdownText, playerNames)

        player1Selector.setAdapter(adapter)
        player2Selector.setAdapter(adapter)

        player1Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer1 = allPlayers.find { it.name.equals(selectedName, ignoreCase = true) }
            setupSeasonSpinner(1, selectedPlayer1)
        }

        player2Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer2 = allPlayers.find { it.name.equals(selectedName, ignoreCase = true) }
            setupSeasonSpinner(2, selectedPlayer2)
        }
    }

    private fun setupSeasonSpinner(playerIndex: Int, profile: PlayerProfile?) {
        val spinner = if (playerIndex == 1) player1SeasonSpinner else player2SeasonSpinner
        if (profile == null) {
            spinner.adapter = null
            return
        }

        val seasons = profile.seasons.sortedByDescending { it.season }
        val seasonLabels = seasons.map { it.season.toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (playerIndex == 1) {
                    selectedSeason1 = seasons[position]
                } else {
                    selectedSeason2 = seasons[position]
                }
                updateComparisonUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        if (playerIndex == 1) selectedSeason1 = seasons.firstOrNull()
        else selectedSeason2 = seasons.firstOrNull()
        
        updateComparisonUI()
    }

    private fun updateComparisonUI() {
        val p1 = selectedPlayer1
        val p2 = selectedPlayer2
        val s1 = selectedSeason1
        val s2 = selectedSeason2

        // make sure both players have been selected
        if (p1 != null && p2 != null && s1 != null && s2 != null) {
            comparisonLayout.visibility = View.VISIBLE
            
            player1Name.text = p1.name
            player2Name.text = p2.name
            player1Team.text = s1.team
            player2Team.text = s2.team
            
            val color1 = getTeamColor(s1.team)
            val color2 = getTeamColor(s2.team)

            player1ColorBar.setBackgroundColor(color1)
            player2ColorBar.setBackgroundColor(color2)

            // load each player's image
            loadPlayerImage(s1.imageUrl, player1Image)
            loadPlayerImage(s2.imageUrl, player2Image)

            // update the stats for each player
            applyStats(s1, color1, p1Stats)
            applyStats(s2, color2, p2Stats)

        } else {
            comparisonLayout.visibility = View.GONE
        }
    }
    // add the player images using glide, circular crop for aesthetics
    private fun loadPlayerImage(url: String, imageView: ImageView) {
        Glide.with(this)
            .load(url)
            .circleCrop()
            .into(imageView)
    }

    // helper function to apply stats to the player
    private fun applyStats(player: Player, color: Int, views: PlayerStatViews) {
        views.yards.text = player.passingYards.toInt().toString()
        views.tds.text = player.passingTouchdowns.toInt().toString()
        views.completions.text = player.passingCompletions.toInt().toString()
        views.attempts.text = player.passingAttempts.toInt().toString()
        views.percentage.text = String.format(Locale.US, "%.1f%%", player.completionPercentage)
        views.interceptions.text = player.passingInterceptions.toInt().toString()

        // team color to stats
        val allStatViews = listOf(views.yards, views.tds, views.completions, views.attempts, views.percentage, views.interceptions)
        allStatViews.forEach { it.setTextColor(color) }
    }

    // team colors for fonts
    private fun getTeamColor(teamAbbr: String): Int {
        val colorHex = when (teamAbbr.uppercase()) {
            "ARI" -> "#97233F" // Cardinals
            "ATL" -> "#A71930" // Falcons
            "BAL" -> "#241773" // Ravens
            "BUF" -> "#00338D" // Bills
            "CAR" -> "#0085CA" // Panthers
            "CHI" -> "#0B162A" // Bears
            "CIN" -> "#FB4F14" // Bengals
            "CLE" -> "#311D00" // Browns
            "DAL" -> "#003594" // Cowboys
            "DEN" -> "#FB4F14" // Broncos
            "DET" -> "#0076B6" // Lions
            "GB"  -> "#203731" // Packers
            "HOU" -> "#03202F" // Texans
            "IND" -> "#002C5F" // Colts
            "JAX" -> "#006778" // Jaguars
            "KC"  -> "#E31837" // Chiefs
            "LAC", "SD" -> "#0080C6" // Chargers
            "LAR", "STL" -> "#003594" // Rams
            "LV", "OAK"  -> "#000000" // Raiders
            "MIA" -> "#008E97" // Dolphins
            "MIN" -> "#4F2683" // Vikings
            "NE"  -> "#002244" // Patriots
            "NO"  -> "#D3BC8D" // Saints
            "NYG" -> "#0B2265" // Giants
            "NYJ" -> "#125740" // Jets
            "PHI" -> "#004C54" // Eagles
            "PIT" -> "#FFB612" // Steelers
            "SF"  -> "#AA0000" // 49ers
            "SEA" -> "#002244" // Seahawks
            "TB"  -> "#D50A0A" // Buccaneers
            "TEN" -> "#0C2340" // Titans
            "WAS" -> "#773141" // Commanders
            else -> "#A5ACAF"  // Default NFL Gray
        }
        return Color.parseColor(colorHex)
    }


    // bottom nav bar
    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_compare
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); finish(); true }
                R.id.nav_players -> { startActivity(Intent(this, PlayersActivity::class.java)); finish(); true }
                R.id.nav_leaderboards -> { startActivity(Intent(this, LeaderboardActivity::class.java)); true }
                R.id.nav_compare -> true
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); finish(); true }
                else -> false
            }
        }
    }
}
