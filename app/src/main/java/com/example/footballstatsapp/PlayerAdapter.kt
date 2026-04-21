package com.example.footballstatsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Player

class PlayerAdapter(
    private var players: List<Player>,
    private var currentCategory: String = "Yards",
    private val onPlayerClick: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    //track stat label to include on card
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
        val player = players[position]

        holder.playerNameText.text = player.name
        holder.playerTeamText.text = player.team

        holder.playerStatText.text = when(currentDisplayStat) {
            "Yards"       -> "${player.passingYards.toInt()} Yds"
            "TDs"         -> "${player.passingTouchdowns.toInt()} TDs"
            "Completions" -> "${player.passingCompletions.toInt()} Cmp"
            "Attempts"    -> "${player.passingAttempts.toInt()} Att"
            "Percentage"  -> "${player.completionPercentage}%"
            "Ints"        -> "${player.passingInterceptions.toInt()} Ints"
            "Season"      -> "Year: ${player.season}"
            else          -> "${player.passingYards.toInt()} Yds"
        }

        holder.itemView.setOnClickListener {
            onPlayerClick(player)
        }
    }

    override fun getItemCount(): Int = players.size

    fun update_data(new_players: List<Player>, statType: String = "Yards") {
        this.players = new_players
        this.currentDisplayStat = statType
        notifyDataSetChanged()
    }
}