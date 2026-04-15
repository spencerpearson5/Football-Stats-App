package com.example.footballstatsapp.data

<<<<<<< Updated upstream
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
=======
import com.example.footballstatsapp.datamodel.Player
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object PlayerRepository {

    private val database = FirebaseDatabase.getInstance().getReference("seasons")


    //upload list of players in a season to firebase
    fun uploadSeason(year: Int, players: List<Player>) {
        database.child(year.toString()).setValue(players)
            .addOnSuccessListener {
                println("Scraper: Successfully uploaded $year to Firebase")
            }
            .addOnFailureListener {
                println("Scraper: Failed to upload $year: ${it.message}")
            }
    }

    //check for season before scraping (optimizes scraping speed)
    suspend fun isSeasonMissing(year: Int): Boolean {
        return try {
            val snapshot = database.child(year.toString()).get().await()
            !snapshot.exists() || snapshot.childrenCount == 0L
        } catch (e: Exception) {
            true
        }
    }


    fun get_qbs(): Flow<List<Player>> = callbackFlow {
        val listener = database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val allPlayers = mutableListOf<Player>()

                //iterate through desired years
                for (yearSnapshot in snapshot.children) {
                    //loop for all players in that season
                    for (playerSnapshot in yearSnapshot.children) {
                        val player = playerSnapshot.getValue(Player::class.java)
                        if (player != null) {
                            allPlayers.add(player)
                        }
                    }
>>>>>>> Stashed changes
                }
                trySend(allPlayers)
            }
<<<<<<< Updated upstream
            qb_list
        }
=======

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { database.removeEventListener(listener) }
>>>>>>> Stashed changes
    }
}