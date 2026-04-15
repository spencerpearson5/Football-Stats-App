package com.example.footballstatsapp

import android.media.metrics.PlaybackErrorEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Player

<<<<<<< Updated upstream
class PlayerAdapter(private var players: List<Quarterbacks>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
=======
class PlayerAdapter(
    private var players: List<Player>,
    private var currentCategory: String = "Yards",
    private val onPlayerClick: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    //track stat label to include on card
    private var currentDisplayStat: String = currentCategory

>>>>>>> Stashed changes
    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name_text: TextView = view.findViewById(android.R.id.text1)
        val stats_text: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
<<<<<<< Updated upstream
        holder.name_text.text = player.name
        holder.stats_text.text = "${player.team} | Touchdowns: ${player.passing_yards}"
=======

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
>>>>>>> Stashed changes
    }

    override fun getItemCount() = players.size

<<<<<<< Updated upstream
    fun update_data(new_players: List<Quarterbacks>) {
    this.players = new_players
    notifyDataSetChanged()
=======
    fun update_data(new_players: List<Player>, statType: String = "Yards") {
        this.players = new_players
        this.currentDisplayStat = statType
        notifyDataSetChanged()
>>>>>>> Stashed changes
    }
}