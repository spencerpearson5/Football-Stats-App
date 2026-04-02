package com.example.footballstatsapp.data

import com.example.footballstatsapp.datamodel.Quarterbacks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object PlayerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val qbCollection = db.collection("quarterbacks")

    /**
     * Fetches QB stats from Firestore.
     */
    suspend fun get_qbs(): List<Quarterbacks> {
        return try {
            val snapshot = qbCollection.get().await()
            snapshot.toObjects(Quarterbacks::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Scrapes data from the web and updates Firestore.
     * This ensures the database is kept up to date.
     */
    suspend fun refresh_stats_in_firestore() {
        withContext(Dispatchers.IO) {
            try {
                // Scraping logic (NFL.com passing stats)
                val url = "https://www.nfl.com/stats/player-stats/category/passing/2025/reg/all/passingyards/desc"
                val doc = Jsoup.connect(url).get()
                val rows = doc.select("tbody tr")

                for (row in rows.take(15)) {
                    val qb = Quarterbacks(
                        name = row.select(".d3-o-player-fullname").text().trim(),
                        team = row.select(".d3-o-player-team").text().trim(),
                        // Column 2 is Pass Yards, Column 7 is Passing TDs
                        passing_yards = row.select("td:nth-child(2)").text().trim(),
                        passing_touchdowns = row.select("td:nth-child(7)").text().trim()
                    )
                    
                    // Use name as document ID to avoid duplicates
                    if (qb.name.isNotEmpty()) {
                        qbCollection.document(qb.name).set(qb).await()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
