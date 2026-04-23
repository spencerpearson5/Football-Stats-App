package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlayerProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var seasonSpinner: Spinner
    private lateinit var graphContainer: LinearLayout

    private lateinit var playerNameText: TextView
    private lateinit var teamText: TextView
    private lateinit var passingYardsText: TextView
    private lateinit var passingTDText: TextView
    private lateinit var completionsText: TextView
    private lateinit var attemptsText: TextView
    private lateinit var compPercentageText: TextView
    private lateinit var interceptionsText: TextView
    private lateinit var playerImage: ShapeableImageView

    private val database = FirebaseDatabase.getInstance().getReference("seasons")
    private var playerSeasons: List<Player> = emptyList()

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

        seasonSpinner = findViewById(R.id.seasonSpinner)
        graphContainer = findViewById(R.id.graphContainer)

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
                    renderCareerTdGraph()
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

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            seasonLabels
        )
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        seasonSpinner.adapter = spinnerAdapter
        seasonSpinner.setSelection(0)
        updateSeasonStats(playerSeasons[0])

        seasonSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateSeasonStats(playerSeasons[position])
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }
    }

    private fun updateSeasonStats(selectedSeason: Player) {
        val fullTeamName = getTeamFullName(selectedSeason.team)
        teamText.text = "$fullTeamName • ${selectedSeason.season}"

        if (selectedSeason.imageUrl.isNotEmpty()) {
            Glide.with(this@PlayerProfileActivity)
                .load(selectedSeason.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(Target.SIZE_ORIGINAL)
                .into(playerImage)
        } else {
            playerImage.setImageDrawable(null)
        }

        passingYardsText.text = selectedSeason.passingYards.toInt().toString()
        passingTDText.text = selectedSeason.passingTouchdowns.toInt().toString()
        completionsText.text = selectedSeason.passingCompletions.toInt().toString()
        attemptsText.text = selectedSeason.passingAttempts.toInt().toString()
        compPercentageText.text =
            String.format("%.1f%%", selectedSeason.completionPercentage)
        interceptionsText.text =
            selectedSeason.passingInterceptions.toInt().toString()
    }

    private fun renderCareerTdGraph() {
        graphContainer.removeAllViews()

        val ascendingSeasons = playerSeasons.sortedBy { it.season }
        val maxTouchdowns = ascendingSeasons
            .maxOfOrNull { it.passingTouchdowns.toInt() }
            ?.coerceAtLeast(1) ?: 1

        for (season in ascendingSeasons) {
            val column = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(56),
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    marginEnd = dpToPx(10)
                }
            }

            val valueText = TextView(this).apply {
                text = season.passingTouchdowns.toInt().toString()
                textSize = 12f
                setTextColor(getColor(R.color.blue_primary))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                gravity = Gravity.CENTER
            }

            val tdCount = season.passingTouchdowns.toInt()
            val barHeightDp = if (tdCount <= 0) {
                12
            } else {
                val progress = tdCount.toFloat() / maxTouchdowns.toFloat()
                (24 + progress * 120).toInt()
            }

            val barView = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(28),
                    dpToPx(barHeightDp)
                ).apply {
                    topMargin = dpToPx(8)
                }
                setBackgroundColor(getColor(R.color.blue_primary))
            }

            val yearText = TextView(this).apply {
                text = season.season.toString()
                textSize = 12f
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
            "WAS" -> "Commanders"
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