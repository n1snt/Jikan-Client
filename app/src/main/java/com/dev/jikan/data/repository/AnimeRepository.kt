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
            val response = apiService.getAnimeById(malId)
            if (response.isSuccessful) {
                val anime = response.body()?.data
                if (anime != null) {
                    emit(Result.success(anime))
                } else {
                    emit(Result.failure(Exception("Anime not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to fetch anime details: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun refreshAnimeList() {
        // For now, just a placeholder for future offline support
    }
}
