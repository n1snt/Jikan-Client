package com.dev.jikan.data.repository

import com.dev.jikan.data.model.Anime
import com.dev.jikan.data.remote.JikanApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnimeRepository(
    private val apiService: JikanApiService
) {

    fun getTopAnime(): Flow<Result<List<Anime>>> = flow {
        try {
            val response = apiService.getTopAnime()
            if (response.isSuccessful) {
                val animeList = response.body()?.data ?: emptyList()
                println("DEBUG: Fetched ${animeList.size} anime items")
                animeList.take(3).forEach { anime ->
                    println("DEBUG: Anime - Title: '${anime.title}', malId: ${anime.malId}")
                }
                emit(Result.success(animeList))
            } else {
                emit(Result.failure(Exception("Failed to fetch anime: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getAnimeById(malId: Int): Flow<Result<Anime>> = flow {
        try {
            // Validate anime ID
            if (malId <= 0) {
                emit(Result.failure(Exception("Invalid anime ID: $malId")))
                return@flow
            }

            // Try the full endpoint first, then fallback to regular endpoint
            val response = try {
                apiService.getAnimeFull(malId)
            } catch (e: Exception) {
                apiService.getAnimeById(malId)
            }
            if (response.isSuccessful) {
                val anime = response.body()?.data
                if (anime != null) {
                    emit(Result.success(anime))
                } else {
                    emit(Result.failure(Exception("Anime not found")))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Bad Request - Invalid anime ID: $malId"
                    404 -> "Anime not found with ID: $malId"
                    429 -> "Rate limit exceeded. Please try again later."
                    else -> "Failed to fetch anime details: ${response.code()} - ${response.message()}"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception("Network error: ${e.message}")))
        }
    }

    suspend fun refreshAnimeList() {
        // For now, just a placeholder for future offline support
    }
}
