package com.example.footballstatsapp

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Quarterbacks

class LeaderboardChartAdapter(
    private var players: List<Quarterbacks>,
    private var currentStat: String
) : RecyclerView.Adapter<LeaderboardChartAdapter.ChartViewHolder>() {

    class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val barView: View = view.findViewById(R.id.barView)
        val valueText: TextView = view.findViewById(R.id.barValueText)
        val nameText: TextView = view.findViewById(R.id.barNameText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_bar_chart, parent, false)
        return ChartViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        val player = players[position]

        holder.nameText.text = player.name.split(" ").last()

        val value = when (currentStat) {
            "Yards" -> player.passing_yards.toIntOrNull() ?: 0
            "TDs" -> player.passing_touchdowns.toIntOrNull() ?: 0
            "Completions" -> player.completions.toIntOrNull() ?: 0
            "Attempts" -> player.attempts.toIntOrNull() ?: 0
            "Ints" -> player.interceptions.toIntOrNull() ?: 0
            "Percentage" -> player.completion_percentage.replace("%", "").toFloatOrNull()?.toInt() ?: 0
            else -> 0
        }
        holder.valueText.text = value.toString()

        val maxValueInList = when (currentStat) {
            "Yards" -> players.maxOfOrNull { it.passing_yards.toIntOrNull() ?: 1 } ?: 1
            "TDs" -> players.maxOfOrNull { it.passing_touchdowns.toIntOrNull() ?: 1 } ?: 1
            "Percentage" -> 100 // Scale percentage out of 100
            else -> players.maxOfOrNull {
                when(currentStat) {
                    "Ints" -> it.interceptions.toIntOrNull() ?: 1
                    "Completions" -> it.completions.toIntOrNull() ?: 1
                    else -> it.attempts.toIntOrNull() ?: 1
                }
            } ?: 1
        }

        val maxBarHeightDp = 140f
        val minBarHeightDp = 10f

        val scaledHeightDp = minBarHeightDp +
                ((value.toFloat() / maxValueInList.toFloat()) * (maxBarHeightDp - minBarHeightDp))

        val params = holder.barView.layoutParams
        params.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            scaledHeightDp,
            holder.itemView.resources.displayMetrics
        ).toInt()
        holder.barView.layoutParams = params
    }

    override fun getItemCount(): Int = players.size

    fun updateData(newPlayers: List<Quarterbacks>, stat: String) {
        this.players = newPlayers
        this.currentStat = stat
        notifyDataSetChanged()
    }
}