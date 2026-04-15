package com.example.footballstatsapp.data

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
                }
                trySend(allPlayers)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { database.removeEventListener(listener) }
    }
}