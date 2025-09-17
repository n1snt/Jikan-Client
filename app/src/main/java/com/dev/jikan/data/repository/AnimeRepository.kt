package com.dev.jikan.data.repository

import com.dev.jikan.data.local.dao.AnimeDao
import com.dev.jikan.data.local.dao.CharacterDao
import com.dev.jikan.data.mapper.AnimeMapper
import com.dev.jikan.data.mapper.CharacterMapper
import com.dev.jikan.data.model.Anime
import com.dev.jikan.data.model.TopAnimeResponse
import com.dev.jikan.data.model.CharacterData
import com.dev.jikan.data.network.NetworkMonitor
import com.dev.jikan.data.remote.JikanApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
    private val apiService: JikanApiService,
    private val animeDao: AnimeDao,
    private val characterDao: CharacterDao,
    private val networkMonitor: NetworkMonitor
) {

    fun getTopAnime(): Flow<Result<List<Anime>>> {
        return combine(
            networkMonitor.networkState(),
            animeDao.getAllAnime()
        ) { isOnline, localEntities ->
            if (isOnline) {
                // Try to fetch from API and update local database
                try {
                    val response = apiService.getTopAnime()
                    if (response.isSuccessful) {
                        val animeList = response.body()?.data ?: emptyList()
                        println("DEBUG: Fetched ${animeList.size} anime items from API")
                        
                        // Save to local database
                        val entities = AnimeMapper.toEntityList(animeList)
                        animeDao.insertAnimeList(entities)
                        
                        Result.success(animeList)
                    } else {
                        // API failed, return local data
                        val localAnime = AnimeMapper.toModelList(localEntities)
                        Result.success(localAnime)
                    }
                } catch (e: Exception) {
                    // Network error, return local data
                    val localAnime = AnimeMapper.toModelList(localEntities)
                    Result.success(localAnime)
                }
            } else {
                // Offline, return local data
                val localAnime = AnimeMapper.toModelList(localEntities)
                Result.success(localAnime)
            }
        }
    }

    suspend fun getTopAnimePage(page: Int): Result<TopAnimeResponse> {
        return try {
            if (networkMonitor.isNetworkAvailable()) {
                val response = apiService.getTopAnime(page = page)
                if (response.isSuccessful) {
                    val topAnimeResponse = response.body()
                    if (topAnimeResponse != null) {
                        println("DEBUG: Fetched page $page with ${topAnimeResponse.data.size} anime items")
                        
                        // Save to local database
                        val entities = AnimeMapper.toEntityList(topAnimeResponse.data)
                        animeDao.insertAnimeList(entities)
                        
                        Result.success(topAnimeResponse)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch anime page $page: ${response.code()}"))
                }
            } else {
                // Offline - return cached data
                val cachedAnime = animeDao.getAnimePage(25, (page - 1) * 25)
                val topAnimeResponse = TopAnimeResponse(
                    data = AnimeMapper.toModelList(cachedAnime),
                    pagination = com.dev.jikan.data.model.Pagination(
                        lastVisiblePage = page,
                        hasNextPage = cachedAnime.size == 25,
                        currentPage = page,
                        items = com.dev.jikan.data.model.PaginationItems(
                            count = cachedAnime.size,
                            total = cachedAnime.size,
                            perPage = 25
                        )
                    )
                )
                Result.success(topAnimeResponse)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAnimeById(malId: Int): Flow<Result<Anime>> {
        return combine(
            networkMonitor.networkState(),
            animeDao.getAnimeByIdFlow(malId)
        ) { isOnline, localEntity ->
            if (isOnline) {
                // Try to fetch from API first
                try {
                    // Validate anime ID
                    if (malId <= 0) {
                        return@combine Result.failure(Exception("Invalid anime ID: $malId"))
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
                            // Save to local database
                            val entity = AnimeMapper.toEntity(anime)
                            animeDao.insertAnime(entity)
                            
                            Result.success(anime)
                        } else {
                            // API returned null, try local data
                            if (localEntity != null) {
                                Result.success(AnimeMapper.toModel(localEntity))
                            } else {
                                Result.failure(Exception("Anime not found"))
                            }
                        }
                    } else {
                        // API failed, try local data
                        if (localEntity != null) {
                            Result.success(AnimeMapper.toModel(localEntity))
                        } else {
                            val errorMessage = when (response.code()) {
                                400 -> "Bad Request - Invalid anime ID: $malId"
                                404 -> "Anime not found with ID: $malId"
                                429 -> "Rate limit exceeded. Please try again later."
                                else -> "Failed to fetch anime details: ${response.code()} - ${response.message()}"
                            }
                            Result.failure(Exception(errorMessage))
                        }
                    }
                } catch (e: Exception) {
                    // Network error, try local data
                    if (localEntity != null) {
                        Result.success(AnimeMapper.toModel(localEntity))
                    } else {
                        Result.failure(Exception("Network error: ${e.message}"))
                    }
                }
            } else {
                // Offline, return local data
                if (localEntity != null) {
                    Result.success(AnimeMapper.toModel(localEntity))
                } else {
                    Result.failure(Exception("Anime not found in offline cache"))
                }
            }
        }
    }

    suspend fun refreshAnimeList() {
        if (networkMonitor.isNetworkAvailable()) {
            try {
                // Fetch fresh data from API
                val response = apiService.getTopAnime()
                if (response.isSuccessful) {
                    val animeList = response.body()?.data ?: emptyList()
                    
                    // Update local database
                    val entities = AnimeMapper.toEntityList(animeList)
                    animeDao.insertAnimeList(entities)
                    
                    println("DEBUG: Refreshed anime list with ${animeList.size} items")
                }
            } catch (e: Exception) {
                println("DEBUG: Failed to refresh anime list: ${e.message}")
            }
        }
    }

    suspend fun syncOfflineData() {
        if (networkMonitor.isNetworkAvailable()) {
            try {
                // Get stale data (older than 1 hour)
                val cutoffTime = System.currentTimeMillis() - (60 * 60 * 1000)
                val staleAnime = animeDao.getStaleAnime(cutoffTime)
                
                for (entity in staleAnime) {
                    try {
                        val response = apiService.getAnimeById(entity.malId)
                        if (response.isSuccessful) {
                            val anime = response.body()?.data
                            if (anime != null) {
                                val updatedEntity = AnimeMapper.toEntity(anime)
                                animeDao.insertAnime(updatedEntity)
                            }
                        }
                    } catch (e: Exception) {
                        println("DEBUG: Failed to sync anime ${entity.malId}: ${e.message}")
                    }
                }
                
                println("DEBUG: Synced ${staleAnime.size} stale anime items")
            } catch (e: Exception) {
                println("DEBUG: Failed to sync offline data: ${e.message}")
            }
        }
    }

    fun searchAnime(query: String): Flow<Result<List<Anime>>> {
        return animeDao.searchAnime(query).map { entities ->
            val animeList = AnimeMapper.toModelList(entities)
            Result.success(animeList)
        }
    }

    fun getAnimeCharacters(animeMalId: Int): Flow<Result<List<CharacterData>>> {
        return combine(
            networkMonitor.networkState(),
            characterDao.getCharactersByAnimeId(animeMalId)
        ) { isOnline, localEntities ->
            if (isOnline) {
                // Try to fetch from API and update local database
                try {
                    val response = apiService.getAnimeCharacters(animeMalId)
                    if (response.isSuccessful) {
                        val characterList = response.body()?.data ?: emptyList()
                        println("DEBUG: Fetched ${characterList.size} characters for anime $animeMalId")
                        
                        // Save to local database
                        val entities = CharacterMapper.toEntityList(characterList, animeMalId)
                        characterDao.insertCharacterList(entities)
                        
                        Result.success(characterList)
                    } else {
                        // API failed, return local data
                        val localCharacters = CharacterMapper.toModelList(localEntities)
                        Result.success(localCharacters)
                    }
                } catch (e: Exception) {
                    // Network error, return local data
                    val localCharacters = CharacterMapper.toModelList(localEntities)
                    Result.success(localCharacters)
                }
            } else {
                // Offline, return local data
                val localCharacters = CharacterMapper.toModelList(localEntities)
                Result.success(localCharacters)
            }
        }
    }

    suspend fun refreshAnimeCharacters(animeMalId: Int) {
        if (networkMonitor.isNetworkAvailable()) {
            try {
                // Fetch fresh character data from API
                val response = apiService.getAnimeCharacters(animeMalId)
                if (response.isSuccessful) {
                    val characterList = response.body()?.data ?: emptyList()
                    
                    // Update local database
                    val entities = CharacterMapper.toEntityList(characterList, animeMalId)
                    characterDao.insertCharacterList(entities)
                    
                    println("DEBUG: Refreshed characters for anime $animeMalId with ${characterList.size} items")
                }
            } catch (e: Exception) {
                println("DEBUG: Failed to refresh characters for anime $animeMalId: ${e.message}")
            }
        }
    }

    suspend fun syncCharacterData() {
        if (networkMonitor.isNetworkAvailable()) {
            try {
                // Get stale character data (older than 1 hour)
                val cutoffTime = System.currentTimeMillis() - (60 * 60 * 1000)
                val staleCharacters = characterDao.getStaleCharacters(cutoffTime)
                
                for (entity in staleCharacters) {
                    try {
                        val response = apiService.getAnimeCharacters(entity.animeMalId)
                        if (response.isSuccessful) {
                            val characterList = response.body()?.data ?: emptyList()
                            val updatedEntities = CharacterMapper.toEntityList(characterList, entity.animeMalId)
                            characterDao.insertCharacterList(updatedEntities)
                        }
                    } catch (e: Exception) {
                        println("DEBUG: Failed to sync characters for anime ${entity.animeMalId}: ${e.message}")
                    }
                }
                
                println("DEBUG: Synced ${staleCharacters.size} stale character items")
            } catch (e: Exception) {
                println("DEBUG: Failed to sync character data: ${e.message}")
            }
        }
    }
}