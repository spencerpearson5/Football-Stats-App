package com.example.footballstatsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Quarterbacks

class PlayerAdapter(
    private var players: List<Quarterbacks>,
    private val onClick: (Quarterbacks) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    private var currentDisplayStat: String = "Yards"

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
            "Yards"       -> "${player.passing_yards} Yds"
            "TDs"         -> "${player.passing_touchdowns} TDs"
            "Completions" -> "${player.completions} Comp"
            "Attempts"    -> "${player.attempts} Att"
            "Percentage"  -> player.completion_percentage // Already has %
            "Ints"        -> "${player.interceptions} Ints"
            else          -> player.passing_yards
        }

        holder.itemView.setOnClickListener {
            onClick(player)
        }
    }

    override fun getItemCount(): Int = players.size

    fun update_data(new_players: List<Quarterbacks>, statType: String = "Yards") {
        this.players = new_players
        this.currentDisplayStat = statType
        notifyDataSetChanged()
    }
}