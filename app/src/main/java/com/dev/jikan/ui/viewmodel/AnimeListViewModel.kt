package com.dev.jikan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.jikan.data.model.Anime
import com.dev.jikan.data.network.NetworkMonitor
import com.dev.jikan.data.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val repository: AnimeRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimeListUiState())
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

    val networkState = networkMonitor.networkState()

    private var currentPage = 1
    private var hasNextPage = true
    private val maxItems = 300

    init {
        loadAnimeList()
    }

    fun loadAnimeList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getTopAnimePage(1)
            result.fold(
                onSuccess = { topAnimeResponse ->
                    val animeList = topAnimeResponse.data
                    currentPage = topAnimeResponse.pagination.currentPage
                    hasNextPage = topAnimeResponse.pagination.hasNextPage

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        animeList = animeList,
                        hasNextPage = hasNextPage,
                        currentPage = currentPage,
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

    fun loadMoreAnime() {
        if (!hasNextPage || _uiState.value.isLoadingMore || _uiState.value.animeList.size >= maxItems) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true, paginationError = null)

            val nextPage = currentPage + 1
            val result = repository.getTopAnimePage(nextPage)

            result.fold(
                onSuccess = { topAnimeResponse ->
                    val newAnimeList = topAnimeResponse.data
                    currentPage = topAnimeResponse.pagination.currentPage
                    hasNextPage = topAnimeResponse.pagination.hasNextPage

                    // Accumulate anime lists and apply memory limit
                    val updatedList = (_uiState.value.animeList + newAnimeList).take(maxItems)

                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        animeList = updatedList,
                        hasNextPage = hasNextPage && updatedList.size < maxItems,
                        currentPage = currentPage,
                        paginationError = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        paginationError = error.message ?: "Failed to load more anime"
                    )
                }
            )
        }
    }

    fun retryLoadMore() {
        loadMoreAnime()
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

    fun clearPaginationError() {
        _uiState.value = _uiState.value.copy(paginationError = null)
    }
}

data class AnimeListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val animeList: List<Anime> = emptyList(),
    val hasNextPage: Boolean = true,
    val currentPage: Int = 1,
    val error: String? = null,
    val paginationError: String? = null
)
