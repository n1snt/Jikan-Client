package com.dev.jikan.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dev.jikan.data.local.entity.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Query("SELECT * FROM anime ORDER BY rank ASC, popularity ASC")
    fun getAllAnime(): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM anime WHERE malId = :malId")
    suspend fun getAnimeById(malId: Int): AnimeEntity?

    @Query("SELECT * FROM anime WHERE malId = :malId")
    fun getAnimeByIdFlow(malId: Int): Flow<AnimeEntity?>

    @Query("SELECT * FROM anime ORDER BY rank ASC, popularity ASC LIMIT :limit OFFSET :offset")
    suspend fun getAnimePage(limit: Int, offset: Int): List<AnimeEntity>

    @Query("SELECT COUNT(*) FROM anime")
    suspend fun getAnimeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeList(animeList: List<AnimeEntity>)

    @Update
    suspend fun updateAnime(anime: AnimeEntity)

    @Delete
    suspend fun deleteAnime(anime: AnimeEntity)

    @Query("DELETE FROM anime")
    suspend fun deleteAllAnime()

    @Query("UPDATE anime SET lastUpdated = :timestamp WHERE malId = :malId")
    suspend fun updateLastUpdated(malId: Int, timestamp: Long)

    @Query("SELECT * FROM anime WHERE lastUpdated < :cutoffTime")
    suspend fun getStaleAnime(cutoffTime: Long): List<AnimeEntity>

    @Query("SELECT * FROM anime WHERE title LIKE '%' || :query || '%' OR titleEnglish LIKE '%' || :query || '%' ORDER BY rank ASC, popularity ASC")
    fun searchAnime(query: String): Flow<List<AnimeEntity>>
}
