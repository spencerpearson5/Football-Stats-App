package com.example.footballstatsapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.PlayerProfile

class PlayerAdapter(
    private var players: List<PlayerProfile>,
    private var currentCategory: String = "Yards",
    private val onPlayerClick: (PlayerProfile) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    private var currentDisplayStat: String = currentCategory

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerNameText: TextView = view.findViewById(R.id.playerNameText)
        val playerTeamText: TextView = view.findViewById(R.id.playerTeamText)
        val playerStatText: TextView = view.findViewById(R.id.playerStatText)
        val playerColorBar: View = view.findViewById(R.id.playerColorBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val playerProfile = players[position]
        val latestSeason = playerProfile.latestSeason
        val teamColor = getTeamColor(latestSeason.team)

        holder.playerNameText.text = playerProfile.name
        holder.playerTeamText.text =
            "${latestSeason.team} • ${playerProfile.seasons.size} seasons"

        holder.playerStatText.text = when (currentDisplayStat) {
            "Yards"       -> "${latestSeason.passingYards.toInt()} Yds"
            "TDs"         -> "${latestSeason.passingTouchdowns.toInt()} TDs"
            "Completions" -> "${latestSeason.passingCompletions.toInt()} Cmp"
            "Attempts"    -> "${latestSeason.passingAttempts.toInt()} Att"
            "Percentage"  -> "${latestSeason.completionPercentage}%"
            "Ints"        -> "${latestSeason.passingInterceptions.toInt()} Ints"
            "Season"      -> "Latest: ${latestSeason.season}"
            else          -> "${latestSeason.passingYards.toInt()} Yds"
        }

        // Apply team color to the bar and the stat text
        holder.playerColorBar.setBackgroundColor(teamColor)
        holder.playerStatText.setTextColor(teamColor)

        holder.itemView.setOnClickListener {
            onPlayerClick(playerProfile)
        }
    }

    override fun getItemCount(): Int = players.size

    fun update_data(new_players: List<PlayerProfile>, statType: String = "Yards") {
        this.players = new_players
        this.currentDisplayStat = statType
        notifyDataSetChanged()
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
            "LAC", "SD" -> "#0080C6" // Chargers current & prerelocation
            "LAR", "STL" -> "#003594" // Rams current & prereolcation
            "LV", "OAK"  -> "#777f85" // Raiders current & prereolcation
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
}