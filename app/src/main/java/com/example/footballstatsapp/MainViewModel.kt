package com.example.footballstatsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatsapp.data.PlayerRepository
import com.example.footballstatsapp.datamodel.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isSyncing.value = true
            
            // Then fetch the data from Firebase Realtime Database
            PlayerRepository.get_qbs().collect { dataList ->
                _players.value = dataList
                _isSyncing.value = false
            }
        }
    }
}
