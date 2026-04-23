package com.example.footballstatsapp

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
        val latestSeason = playerProfile.latestSeason

        holder.playerNameText.text = playerProfile.name
        holder.playerTeamText.text = when (latestSeason.team?.uppercase()) {
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
            "WAS" -> "Commanders"
            else -> latestSeason.team ?: "Team Unknown"
        }
        holder.playerStatText.text =
            "${latestSeason.passingTouchdowns.toInt()} TDs"

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