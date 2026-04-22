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
        val playerNameText: TextView = view.findViewById(R.id.playerNameText)
        val playerTeamText: TextView = view.findViewById(R.id.playerTeamText)
        val playerStatText: TextView = view.findViewById(R.id.playerStatText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeaturedPlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return FeaturedPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeaturedPlayerViewHolder, position: Int) {
        val playerProfile = players[position]
        val latestSeason = playerProfile.latestSeason

        holder.playerNameText.text = playerProfile.name
        holder.playerTeamText.text = latestSeason.team
        holder.playerStatText.text =
            "${latestSeason.passingYards.toInt()} Yds"

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