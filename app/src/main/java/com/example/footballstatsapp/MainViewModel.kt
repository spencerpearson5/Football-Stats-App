package com.example.footballstatsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatsapp.data.PlayerRepository
import com.example.footballstatsapp.datamodel.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.footballstatsapp.datamodel.PlayerProfile

class MainViewModel : ViewModel() {

    private val _players = MutableStateFlow<List<PlayerProfile>>(emptyList())
    val players: StateFlow<List<PlayerProfile>> = _players

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isSyncing.value = true
            
            // Then fetch the data from Firebase Realtime Database
            PlayerRepository.getQbProfiles().collect { dataList ->
                _players.value = dataList
                _isSyncing.value = false
            }
        }
    }
}
