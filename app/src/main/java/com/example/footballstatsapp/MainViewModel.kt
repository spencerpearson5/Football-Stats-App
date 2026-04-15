package com.example.footballstatsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballstatsapp.data.PlayerRepository
import com.example.footballstatsapp.data.ScraperRepository
import com.example.footballstatsapp.datamodel.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
<<<<<<< Updated upstream
            _players.value = PlayerRepository.get_qbs()
        }
    }

=======
            PlayerRepository.get_qbs().collect { dataList ->
                if (dataList.isEmpty()) {
                    println("Debug: Flow received, but list is empty")
                } else {
                    println("Debug: Successfully received ${dataList.size} players")
                    _players.value = dataList
                }
            }
        }
    }
>>>>>>> Stashed changes
}