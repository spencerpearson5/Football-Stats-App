package com.example.footballstatsapp

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val playerProfile = players[position]
        val latestSeason = playerProfile.latestSeason

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
}