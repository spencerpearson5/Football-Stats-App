package com.example.footballstatsapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class PlayerProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var seasonSpinner: Spinner
    private lateinit var graphStatSpinner: Spinner
    private lateinit var graphContainer: LinearLayout
    private lateinit var graphTitleText: TextView
    private lateinit var graphLabelText: TextView

    private lateinit var playerNameText: TextView
    private lateinit var teamText: TextView
    private lateinit var passingYardsText: TextView
    private lateinit var passingTDText: TextView
    private lateinit var completionsText: TextView
    private lateinit var attemptsText: TextView
    private lateinit var compPercentageText: TextView
    private lateinit var interceptionsText: TextView
    private lateinit var playerImage: ImageView
    private lateinit var playerColorBar: View

    private val database = FirebaseDatabase.getInstance().getReference("seasons")
    private var playerSeasons: List<Player> = emptyList()

    private val statOptions = listOf("Yards", "Touchdowns", "Completions", "Attempts", "Comp %", "Interceptions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_profile)

        playerNameText = findViewById(R.id.playerNameText)
        teamText = findViewById(R.id.teamText)
        passingYardsText = findViewById(R.id.passingYardsText)
        passingTDText = findViewById(R.id.passingTDText)
        completionsText = findViewById(R.id.completionsText)
        attemptsText = findViewById(R.id.attemptsText)
        compPercentageText = findViewById(R.id.compPercentageText)
        interceptionsText = findViewById(R.id.interceptionsText)
        playerImage = findViewById(R.id.playerImage)
        playerColorBar = findViewById(R.id.playerColorBar)

        // new graph components
        seasonSpinner = findViewById(R.id.seasonSpinner)
        // new graph components
        graphStatSpinner = findViewById(R.id.graphStatSpinner)
        graphContainer = findViewById(R.id.graphContainer)
        graphTitleText = findViewById(R.id.graphTitleText) 
        graphLabelText = findViewById(R.id.graphLabelText)

        val playerName = intent.getStringExtra("player_name") ?: "Unknown Player"

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val matchingSeasons = mutableListOf<Player>()

                for (yearSnapshot in snapshot.children) {
                    for (playerSnapshot in yearSnapshot.children) {
                        val player = playerSnapshot.getValue(Player::class.java)
                        if (player != null && player.name == playerName) {
                            matchingSeasons.add(player)
                        }
                    }
                }

                if (matchingSeasons.isNotEmpty()) {
                    playerSeasons = matchingSeasons.sortedByDescending { it.season }
                    playerNameText.text = playerName
                    setupSeasonSpinner()
                    setupGraphStatSpinner()
                } else {
                    showEmptyState(playerName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showEmptyState(playerName)
            }
        })

        setupNavigation()
    }

    private fun setupSeasonSpinner() {
        val seasonLabels = playerSeasons.map { it.season.toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        seasonSpinner.adapter = adapter
        
        seasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSeasonStats(playerSeasons[position])
                // refresh color for team of selected year
                val selectedStat = graphStatSpinner.selectedItem?.toString() ?: statOptions[0]
                renderCareerStatGraph(selectedStat)
            }
            override fun onNothingSelected(parent: AdapterView<*>?
            ) {

            }
        }
    }

    // spinner to select desired stat fto be graphed
    private fun setupGraphStatSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        graphStatSpinner.adapter = adapter

        graphStatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStat = statOptions[position]
                graphTitleText.text = "Career $selectedStat Graph"
                graphLabelText.text = "$selectedStat by Season"
                renderCareerStatGraph(selectedStat)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSeasonStats(selectedSeason: Player) {
        val fullTeamName = getTeamFullName(selectedSeason.team)
        teamText.text = "$fullTeamName • ${selectedSeason.season}"

        if (selectedSeason.imageUrl.isNotEmpty()) {
            Glide.with(this@PlayerProfileActivity)
                .load(selectedSeason.imageUrl)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(playerImage)
        } else {
            playerImage.setImageDrawable(null)
        }

        val teamColor = getTeamColor(selectedSeason.team)
        
        passingYardsText.text = selectedSeason.passingYards.toInt().toString()
        passingTDText.text = selectedSeason.passingTouchdowns.toInt().toString()
        completionsText.text = selectedSeason.passingCompletions.toInt().toString()
        attemptsText.text = selectedSeason.passingAttempts.toInt().toString()
        compPercentageText.text =
            String.format(Locale.US, "%.1f%%", selectedSeason.completionPercentage)
        interceptionsText.text =
            selectedSeason.passingInterceptions.toInt().toString()

        // apply team color
        playerColorBar.setBackgroundColor(teamColor)
        passingYardsText.setTextColor(teamColor)
        passingTDText.setTextColor(teamColor)
        completionsText.setTextColor(teamColor)
        attemptsText.setTextColor(teamColor)
        compPercentageText.setTextColor(teamColor)
        interceptionsText.setTextColor(teamColor)
    }

    // career progression graph for selected stat
    private fun renderCareerStatGraph(statType: String) {
        graphContainer.removeAllViews()

        val ascendingSeasons = playerSeasons.sortedBy { it.season }

        val maxValue = ascendingSeasons.maxOfOrNull { getStatValue(it, statType) }?.coerceAtLeast(1.0) ?: 1.0

        for (season in ascendingSeasons) {
            val value = getStatValue(season, statType)
            val teamColor = getTeamColor(season.team)
            
            val column = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(42),
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    marginEnd = dpToPx(6)
                }
            }

            val valueText = TextView(this).apply {
                text = if (statType == "Comp %") String.format(Locale.US, "%.0f", value) else value.toInt().toString()
                textSize = 10f
                setTextColor(teamColor)
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = Gravity.CENTER
            }
            val barHeightDp = (24 + (value / maxValue * 120)).toInt()
            val barView = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(20), dpToPx(barHeightDp)).apply { topMargin = dpToPx(4) }
                setBackgroundColor(teamColor)
            }
            val yearText = TextView(this).apply {
                text = season.season.toString().takeLast(2)
                textSize = 10f
                gravity = Gravity.CENTER
                setTextColor(getColor(android.R.color.darker_gray))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dpToPx(8)
                }
            }

            column.addView(valueText)
            column.addView(barView)
            column.addView(yearText)

            graphContainer.addView(column)
        }
    }
    // helper function for stats
    private fun getStatValue(player: Player, type: String): Double {
        return when (type) {
            "Yards" -> player.passingYards
            "Touchdowns" -> player.passingTouchdowns
            "Completions" -> player.passingCompletions
            "Attempts" -> player.passingAttempts
            "Comp %" -> player.completionPercentage
            "Interceptions" -> player.passingInterceptions
            else -> 0.0
        }
    }

    private fun getTeamColor(teamAbbr: String): Int {
        val colorHex = when (teamAbbr.uppercase()) {
            "ARI" -> "#97233F" // Cardinals
            "ATL" -> "#A71930" // Falcons
            "BAL" -> "#241773" // Ravens
            "BUF" -> "#00338D" // Bills
            "CAR" -> "#0085CA" // Panthers
            "CHI" -> "#0B162A" // Bears
            "CIN" -> "#FB4F14" // Bengals
            "CLE" -> "#6e4408" // Browns
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
            "LV", "OAK"  -> "#777f85" // Raiders
            "MIA" -> "#008E97" // Dolphins
            "MIN" -> "#4F2683" // Vikings
            "NE"  -> "#090957" // Patriots
            "NO"  -> "#D3BC8D" // Saints
            "NYG" -> "#0B2265" // Giants
            "NYJ" -> "#125740" // Jets
            "PHI" -> "#004C54" // Eagles
            "PIT" -> "#FFB612" // Steelers
            "SF"  -> "#AA0000" // 49ers
            "SEA" -> "#002244" // Seahawks
            "TB"  -> "#D50A0A" // Buccaneers
            "TEN" -> "#5da6f0" // Titans
            "WAS", "WSH" -> "#773141" // Commanders
            else -> "#A5ACAF"  // Default NFL Gray
        }
        return Color.parseColor(colorHex)
    }

    private fun showEmptyState(playerName: String) {
        playerNameText.text = playerName
        teamText.text = "No data found"
        passingYardsText.text = "0"
        passingTDText.text = "0"
        completionsText.text = "0"
        attemptsText.text = "0"
        compPercentageText.text = "0.0%"
        interceptionsText.text = "0"
        playerImage.setImageDrawable(null)
        playerColorBar.setBackgroundColor(Color.LTGRAY)
        seasonSpinner.isEnabled = false
        graphContainer.removeAllViews()
    }

    private fun setupNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_players

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    true
                }

                R.id.nav_leaderboards -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    true
                }

                R.id.nav_players -> {
                    startActivity(Intent(this, PlayersActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
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

    private fun getTeamFullName(initials: String?): String {
        return when (initials?.uppercase()) {
            "ARI" -> "Cardinals"
            "ATL" -> "Falcons"
            "BAL" -> "Ravens"
            "BUF" -> "Bills"
            "CAR" -> "Panthers"
            "CHI" -> "Bears"
            "CIN" -> "Bengals"
            "CLE" -> "Browns"
            "DAL" -> "Cowboys"
            "DEN" -> "Broncos"
            "DET" -> "Lions"
            "GB" -> "Packers"
            "HOU" -> "Texans"
            "IND" -> "Colts"
            "JAX" -> "Jaguars"
            "KC" -> "Chiefs"
            "LAC", "SD" -> "Chargers"
            "LAR", "STL" -> "Rams"
            "LV", "OAK" -> "Raiders"
            "MIA" -> "Dolphins"
            "MIN" -> "Vikings"
            "NE" -> "Patriots"
            "NO" -> "Saints"
            "NYG" -> "Giants"
            "NYJ" -> "Jets"
            "PHI" -> "Eagles"
            "PIT" -> "Steelers"
            "SF" -> "49ers"
            "SEA" -> "Seahawks"
            "TB" -> "Buccaneers"
            "TEN" -> "Titans"
            "WAS", "WSH" -> "Commanders"
            else -> initials ?: "Team Unknown"
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}