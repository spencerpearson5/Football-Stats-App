package com.example.footballstatsapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.PlayerProfile

class FeaturedPlayerAdapter(
    private var players: List<PlayerProfile>,
    private val onClick: (PlayerProfile) -> Unit
) : RecyclerView.Adapter<FeaturedPlayerAdapter.FeaturedPlayerViewHolder>() {

    class FeaturedPlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerNameText: TextView = view.findViewById(R.id.featuredPlayerName)
        val playerTeamText: TextView = view.findViewById(R.id.featuredPlayerTeam)
        val playerStatText: TextView = view.findViewById(R.id.featuredPlayerStat)
        val colorBar: View = view.findViewById(R.id.featuredPlayerColorBar)
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
            "LV", "OAK"  -> "#777f85" // Raiders
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
            "WAS","WSH" -> "#773141" // Commanders
            else -> "#A5ACAF"  // Default NFL Gray
        }
        return Color.parseColor(colorHex)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeaturedPlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_featured_player, parent, false)
        return FeaturedPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeaturedPlayerViewHolder, position: Int) {
        val playerProfile = players[position]
        // Pick a random season from the player's career
        val randomSeason = playerProfile.seasons.randomOrNull() ?: playerProfile.latestSeason

        holder.playerNameText.text = playerProfile.name
        
        val teamFullName = when (randomSeason.team.uppercase()) {
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
            "GB"  -> "Packers"
            "HOU" -> "Texans"
            "IND" -> "Colts"
            "JAX" -> "Jaguars"
            "KC"  -> "Chiefs"
            "LAC", "SD" -> "Chargers"
            "LAR", "STL" -> "Rams"
            "LV", "OAK"  -> "Raiders"
            "MIA" -> "Dolphins"
            "MIN" -> "Vikings"
            "NE"  -> "Patriots"
            "NO"  -> "Saints"
            "NYG" -> "Giants"
            "NYJ" -> "Jets"
            "PHI" -> "Eagles"
            "PIT" -> "Steelers"
            "SF"  -> "49ers"
            "SEA" -> "Seahawks"
            "TB"  -> "Buccaneers"
            "TEN" -> "Titans"
            "WAS", "WSH" -> "Commanders"
            else -> randomSeason.team ?: "Team Unknown"
        }

        val teamColor = getTeamColor(randomSeason.team)
        holder.colorBar.setBackgroundColor(teamColor)
        holder.playerStatText.setTextColor(teamColor)

        holder.playerTeamText.text = "${randomSeason.season} $teamFullName"
        holder.playerStatText.text = randomSeason.passingTouchdowns.toInt().toString()

        holder.itemView.setOnClickListener {
            onClick(playerProfile)
        }
    }

    override fun getItemCount(): Int = players.size

    fun updateData(newPlayers: List<PlayerProfile>) {
        players = newPlayers
        notifyDataSetChanged()
    }
}
