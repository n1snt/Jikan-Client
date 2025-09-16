package com.dev.jikan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.jikan.data.model.Anime
import com.dev.jikan.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeListViewModel(
    private val repository: AnimeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AnimeListUiState())
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()
    
    init {
        loadAnimeList()
    }
    
    fun loadAnimeList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getTopAnime().collect { result ->
                result.fold(
                    onSuccess = { animeList ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            animeList = animeList,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred"
                        )
                    }
                )
            }
        }
    }
    
    fun refreshAnimeList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            repository.refreshAnimeList()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AnimeListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val animeList: List<Anime> = emptyList(),
    val error: String? = null
)
