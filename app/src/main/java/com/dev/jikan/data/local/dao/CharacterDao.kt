package com.dev.jikan.data.local.dao

import androidx.room.*
import com.dev.jikan.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters WHERE animeMalId = :animeMalId ORDER BY role ASC, characterName ASC")
    fun getCharactersByAnimeId(animeMalId: Int): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE characterMalId = :characterMalId")
    suspend fun getCharacterById(characterMalId: Int): CharacterEntity?

    @Query("SELECT * FROM characters WHERE characterMalId = :characterMalId")
    fun getCharacterByIdFlow(characterMalId: Int): Flow<CharacterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterList(characterList: List<CharacterEntity>)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE animeMalId = :animeMalId")
    suspend fun deleteCharactersByAnimeId(animeMalId: Int)

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()

    @Query("UPDATE characters SET lastUpdated = :timestamp WHERE characterMalId = :characterMalId")
    suspend fun updateLastUpdated(characterMalId: Int, timestamp: Long)

    @Query("SELECT * FROM characters WHERE lastUpdated < :cutoffTime")
    suspend fun getStaleCharacters(cutoffTime: Long): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE characterName LIKE '%' || :query || '%' ORDER BY role ASC, characterName ASC")
    fun searchCharacters(query: String): Flow<List<CharacterEntity>>
}
