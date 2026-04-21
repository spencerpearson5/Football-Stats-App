package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PlayersActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var alphabetBar: LinearLayout

    private var sortedPlayers: List<Player> = emptyList()
    private val letterViews = mutableMapOf<Char, TextView>()
    private var selectedLetter: Char? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        recyclerView = findViewById(R.id.recyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        alphabetBar = findViewById(R.id.alphabetBar)

        playerAdapter = PlayerAdapter(emptyList()) { player ->
            val intent = Intent(this, PlayerProfileActivity::class.java)
            intent.putExtra("player_name", player.name)
            intent.putExtra("team", player.team)
            intent.putExtra("passing_yards", player.passingYards.toInt().toString())
            intent.putExtra("passing_touchdowns", player.passingTouchdowns.toInt().toString())
            intent.putExtra("completions", player.passingCompletions.toInt().toString())
            intent.putExtra("attempts", player.passingAttempts.toInt().toString())
            intent.putExtra("completion_percentage", player.completionPercentage.toString())
            intent.putExtra("interceptions", player.passingInterceptions.toInt().toString())
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = playerAdapter

        setupAlphabetBar()

        lifecycleScope.launch {
            viewModel.players.collect { players ->
                sortedPlayers = players.sortedBy { it.name.trim().lowercase() }
                playerAdapter.update_data(sortedPlayers)
            }
        }

        setupBottomNavigation()
    }

    private fun setupAlphabetBar() {
        val letters = ('A'..'Z')

        for (letter in letters) {
            val textView = TextView(this).apply {
                text = letter.toString()
                textSize = 10f
                gravity = Gravity.CENTER
                minWidth = dpToPx(24)
                minHeight = dpToPx(24)
                setPadding(0, dpToPx(2), 0, dpToPx(2))
                setTextColor(resources.getColor(R.color.blue_primary, theme))
                background = resources.getDrawable(
                    R.drawable.alphabet_letter_unselected,
                    theme
                )

                setOnClickListener {
                    selectedLetter = letter
                    updateLetterHighlight()
                    scrollToLetter(letter)
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = dpToPx(1)
            params.bottomMargin = dpToPx(1)

            textView.layoutParams = params
            letterViews[letter] = textView
            alphabetBar.addView(textView)
        }
    }

    private fun updateLetterHighlight() {
        for ((letter, textView) in letterViews) {
            if (letter == selectedLetter) {
                textView.background = resources.getDrawable(
                    R.drawable.alphabet_letter_selected,
                    theme
                )
                textView.setTextColor(resources.getColor(R.color.white, theme))
            } else {
                textView.background = resources.getDrawable(
                    R.drawable.alphabet_letter_unselected,
                    theme
                )
                textView.setTextColor(
                    resources.getColor(R.color.blue_primary, theme)
                )
            }
        }
    }

    private fun scrollToLetter(letter: Char) {
        val index = sortedPlayers.indexOfFirst { player ->
            player.name.trim()
                .startsWith(letter.toString(), ignoreCase = true)
        }

        if (index != -1) {
            recyclerView.scrollToPosition(index)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_players
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_players -> true
                R.id.nav_leaderboards -> {
                    val intent = Intent(this, LeaderboardActivity::class.java)
                    startActivity(intent)
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

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
