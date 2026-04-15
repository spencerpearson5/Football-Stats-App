package com.example.footballstatsapp.data

import com.example.footballstatsapp.datamodel.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class ScraperRepository {

    suspend fun syncSeason(year: Int) {
        val players = scrapeESPN(year)
        if (players.isNotEmpty()) {
            // Save the scraped data to Firebase
            PlayerRepository.uploadSeason(year, players)
        }
    }

    suspend fun scrapeESPN(year: Int): List<Player> = withContext(Dispatchers.IO) {
        val url = "https://www.espn.com/nfl/stats/player/_/season/$year/seasontype/2"
        val players = mutableListOf<Player>()

        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .timeout(15000)
                .get()

            val nameRows = doc.select("table.Table--fixed-left tbody tr")
            val statRows = doc.select("div.Table__Scroller table.Table tbody tr")

            for (i in nameRows.indices) {
                val nameCell = nameRows[i].select("td").last()
                val playerName = nameCell?.select("a")?.text() ?: ""
                val team = nameCell?.select("span")?.text() ?: ""

                val cols = statRows[i].select("td")

                if (cols.size >= 13 && playerName.isNotEmpty()) {
                    val pct = cols[4].text().cleanDouble()
                    val yds = cols[5].text().cleanDouble()
                    val cmp = cols[2].text().cleanDouble()
                    val att = cols[3].text().cleanDouble()
                    val tds = cols[9].text().cleanDouble()
                    val ints = cols[10].text().cleanDouble()
                    players.add(Player(
                        name = playerName,
                        team = team,
                        season = year,
                        passingCompletions = cmp,
                        passingAttempts = att,
                        completionPercentage = pct,
                        passingYards = yds,
                        passingTouchdowns = tds,
                        passingInterceptions = ints
                    ))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        players
    }

    fun String.cleanDouble(): Double = this.replace(",", "").toDoubleOrNull() ?: 0.0
}