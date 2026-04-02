package com.example.footballstatsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Quarterbacks

class FeaturedPlayerAdapter(
    private var players: List<Quarterbacks>,
    private val onPlayerClick: (Quarterbacks) -> Unit
) : RecyclerView.Adapter<FeaturedPlayerAdapter.FeaturedPlayerViewHolder>() {

    class FeaturedPlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerNameTextView: TextView =
            view.findViewById(R.id.featuredPlayerName)
        val playerTeamTextView: TextView =
            view.findViewById(R.id.featuredPlayerTeam)
        val playerStatTextView: TextView =
            view.findViewById(R.id.featuredPlayerStat)
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
        val player = players[position]

        holder.playerNameTextView.text = player.name
        holder.playerTeamTextView.text = player.team
        holder.playerStatTextView.text =
            player.passing_touchdowns.toString()

        holder.itemView.setOnClickListener {
            onPlayerClick(player)
        }
    }

    override fun getItemCount(): Int = players.size

    fun updateData(newPlayers: List<Quarterbacks>) {
        players = newPlayers
        notifyDataSetChanged()
    }
}