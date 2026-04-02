package com.example.footballstatsapp

import android.media.metrics.PlaybackErrorEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Quarterbacks

class PlayerAdapter(private var players: List<Quarterbacks>,
                    private val onClick: (Quarterbacks) -> Unit) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
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
        holder.name_text.text = player.name
        holder.stats_text.text =
            "${player.team} | Passing Yards: ${player.passing_yards}"
        holder.itemView.setOnClickListener {
            onClick(players[position])
        }
    }

    override fun getItemCount() = players.size

    fun update_data(new_players: List<Quarterbacks>) {
    this.players = new_players
    notifyDataSetChanged()
    }
}