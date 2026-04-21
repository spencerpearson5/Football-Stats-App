package com.example.footballstatsapp

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballstatsapp.datamodel.Player

class LeaderboardChartAdapter(
    private var players: List<Player>,
    private var currentStat: String
) : RecyclerView.Adapter<LeaderboardChartAdapter.ChartViewHolder>() {

    class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val barView: View = view.findViewById(R.id.barView)
        val valueText: TextView = view.findViewById(R.id.barValueText)
        val nameText: TextView = view.findViewById(R.id.barNameText)
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
            "LAC", "SD" -> "#0080C6" // Chargers current & prerelocation
            "LAR", "STL" -> "#003594" // Rams current & prereolcation
            "LV", "OAK"  -> "#000000" // Raiders current & prereolcation
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
            "WAS" -> "#773141" // Commanders
            else -> "#A5ACAF"  // Default NFL Gray
        }
        return Color.parseColor(colorHex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_bar_chart, parent, false)
        return ChartViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        val player = players[position]

        // set bar color
        holder.barView.setBackgroundColor(getTeamColor(player.team))

        // show last name
        holder.nameText.text = player.name.split(" ").last()

        val value: Double = when (currentStat) {
            "Yards" -> player.passingYards
            "TDs" -> player.passingTouchdowns
            "Completions" -> player.passingCompletions
            "Attempts" -> player.passingAttempts
            "Ints" -> player.passingInterceptions
            "Percentage" -> player.completionPercentage
            else -> 0.0
        }

        holder.valueText.text = if (currentStat == "Percentage") {
            String.format("%.1f", value)
        } else {
            value.toInt().toString()
        }

        // calculate max value for scaling bar heights
        val maxValueInList: Double = when (currentStat) {
            "Yards" -> players.maxOfOrNull { it.passingYards } ?: 1.0
            "TDs" -> players.maxOfOrNull { it.passingTouchdowns } ?: 1.0
            "Percentage" -> 100.0
            "Completions" -> players.maxOfOrNull { it.passingCompletions } ?: 1.0
            "Attempts" -> players.maxOfOrNull { it.passingAttempts } ?: 1.0
            "Ints" -> players.maxOfOrNull { it.passingInterceptions } ?: 1.0
            else -> 1.0
        }.coerceAtLeast(1.0)

        val maxBarHeightDp = 140f
        val minBarHeightDp = 10f
        val scaledHeightDp = minBarHeightDp + ((value.toFloat() / maxValueInList.toFloat()) * (maxBarHeightDp - minBarHeightDp))

        val params = holder.barView.layoutParams
        params.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            scaledHeightDp,
            holder.itemView.resources.displayMetrics
        ).toInt()
        holder.barView.layoutParams = params
    }

    override fun getItemCount(): Int = players.size

    fun updateData(newPlayers: List<Player>, stat: String) {
        this.players = newPlayers
        this.currentStat = stat
        notifyDataSetChanged()
    }
}