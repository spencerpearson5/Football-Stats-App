package com.example.footballstatsapp.data

import  com.example.footballstatsapp.datamodel.Quarterbacks
import org.jsoup.Jsoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PlayerRepository {

    suspend fun get_qbs(): List<Quarterbacks> {
        return withContext(Dispatchers.IO) {
            val qb_list = mutableListOf<Quarterbacks>()
            try {
                val url = "https://www.nfl.com/stats/player-stats/category/passing/2025/reg/all/passingyards/desc"
                val doc = Jsoup.connect(url).get()
                val rows = doc.select("tbody tr")

                for (row in rows.take(15)) {
                    qb_list.add(Quarterbacks(
                        name = row.select(".d3-o-player-fullname").text(),
                        team = row.select(".d3.-o-player-team").text(),
                        passing_touchdowns = row.select("td:nth-child(7)").text(),
                        passing_yards = row.select("td:nth-child(7)").text()
                    ))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            qb_list
        }
    }
}
