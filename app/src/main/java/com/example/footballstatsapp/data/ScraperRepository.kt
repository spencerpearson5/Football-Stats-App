package com.example.footballstatsapp.data

import com.example.footballstatsapp.datamodel.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import android.util.Log

class ScraperRepository {

    private val TAG = "ScraperDebug" //DEBUG

    private fun formatNameForPictureUrl(name: String): String {
        return name.lowercase()
            .replace(".", "")
            .replace("'", "")
            .replace(" ", "-")
    }


    private suspend fun scrapePlayerImage(urlName: String): String = withContext(Dispatchers.IO) {
        if (urlName.isEmpty()) return@withContext ""

        val url = "https://www.nfl.com/players/$urlName/"

        return@withContext try {
            Log.d("ScraperDebug", "Connecting to NFL.com for: $urlName")

            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .timeout(10000)
                .get()

            var src = doc.select("meta[property=og:image]").attr("content")

            if (src.isEmpty()) {
                val imgElement = doc.select("picture img.img-responsive").first()
                    ?: doc.select(".nfl-c-player-header__headshot img").first()
                    ?: doc.select("img[alt*='Headshot']").first()

                src = imgElement?.absUrl("src") ?: ""
            }

            if (src.isNotEmpty()) {
                val highResUrl = src
                    .replace("t_player_profile_landscape", "t_player_profile_headshot_desktop")
                    .replace("t_lazy", "t_player_profile_headshot_desktop")
                    .replace("f_auto", "f_png")

                Log.d("ScraperDebug", "Found High-Res Image for $urlName: $highResUrl")
                highResUrl
            } else {
                Log.e("ScraperDebug", "No image source found on page for $urlName")
                ""
            }
        } catch (e: Exception) {
            Log.e("ScraperDebug", "NFL.com lookup failed for $urlName: ${e.message}")
            ""
        }
    }

    suspend fun syncSeason(year: Int) {
        Log.d(TAG, "Starting sync for year: $year") //DEBUG
        val players = scrapeESPN(year)
        if (players.isNotEmpty()) {
            Log.d(TAG, "Scraped ${players.size} players. Starting Firebase upload...") //DEBUG
            // Save the scraped data to Firebase
            PlayerRepository.uploadSeason(year, players)
        } else {
            Log.e(TAG, "No players scraped for $year") //DEBUG
        }
    }

    suspend fun scrapeESPN(year: Int): List<Player> = withContext(Dispatchers.IO) {
        val url = "https://www.espn.com/nfl/stats/player/_/season/$year/seasontype/2"
        val players = mutableListOf<Player>()

        try {
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(20000)
                .get()

            val nameRows = doc.select(".Table--fixed-left .Table__TR--sm")
            val statRows = doc.select(".Table__Scroller .Table__TR--sm")

            for (i in nameRows.indices) {
                val nameCell = nameRows[i].select("td").last()

                // 1. Get the ESPN Link: "/nfl/player/_/id/4038944/joe-burrow"
                val espnLink = nameCell?.select("a")?.attr("href") ?: ""

                // 2. Extract "joe-burrow"
                val slugName = espnLink.substringAfterLast("/")

                // 3. Optional: Convert "joe-burrow" to "Joe Burrow" for the DB
                val formattedName = slugName.replace("-", " ")
                    .split(" ")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

                val team = nameCell?.select("span")?.text() ?: ""

                // 4. Scrape the image using the slug
                val imageUrl = scrapePlayerImage(slugName)

                val cols = statRows[i].select("td")

                if (cols.size >= 13 && slugName.isNotEmpty()) {
                    players.add(Player(
                        name = formattedName, // Now saves as "Joe Burrow"
                        team = team,
                        season = year,
                        imageUrl = imageUrl,
                        passingCompletions = cols[2].text().cleanDouble(),
                        passingAttempts = cols[3].text().cleanDouble(),
                        completionPercentage = cols[4].text().cleanDouble(),
                        passingYards = cols[5].text().cleanDouble(),
                        passingTouchdowns = cols[9].text().cleanDouble(),
                        passingInterceptions = cols[10].text().cleanDouble()
                    ))
                    Log.d("ScraperDebug", "Saved: $formattedName with image: $imageUrl")
                }

                // Polite delay to prevent IP blocking
                Thread.sleep(500)
            }
        } catch (e: Exception) {
            Log.e("ScraperDebug", "Scrape failed: ${e.message}")
        }
        players
    }

    fun String.cleanDouble(): Double = this.replace(",", "").toDoubleOrNull() ?: 0.0
}