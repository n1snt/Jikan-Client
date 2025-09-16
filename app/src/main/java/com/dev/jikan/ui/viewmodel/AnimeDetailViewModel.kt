package com.dev.jikan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.jikan.data.model.Anime
import com.dev.jikan.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeDetailViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimeDetailUiState())
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    fun loadAnimeDetails(animeId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAnimeById(animeId).collect { result ->
                result.fold(
                    onSuccess = { anime ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            anime = anime,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load anime details"
                        )
                    }
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AnimeDetailUiState(
    val isLoading: Boolean = false,
    val anime: Anime? = null,
    val error: String? = null
)
