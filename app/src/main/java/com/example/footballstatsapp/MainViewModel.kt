package com.example.footballstatsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatsapp.data.PlayerRepository
import com.example.footballstatsapp.datamodel.Quarterbacks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _players = MutableStateFlow<List<Quarterbacks>>(emptyList())
    val players: StateFlow<List<Quarterbacks>> = _players

    init {
        load_stats()
    }

    fun load_stats() {
        viewModelScope.launch {
            // First, scrape the latest data and update Firestore
            PlayerRepository.refresh_stats_in_firestore()
            
            // Then, fetch the updated data from Firestore and update the UI
            _players.value = PlayerRepository.get_qbs()
        }
    }


}