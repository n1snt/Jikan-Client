package com.dev.jikan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dev.jikan.data.local.converter.Converters

@Entity(tableName = "characters")
@TypeConverters(Converters::class)
data class CharacterEntity(
    @PrimaryKey
    val characterMalId: Int,
    val animeMalId: Int,
    val characterName: String,
    val characterUrl: String,
    val role: String,
    
    // Character image URLs (flattened)
    val characterImageUrl: String?,
    val characterSmallImageUrl: String?,
    val characterLargeImageUrl: String?,
    
    // Voice actors data (stored as JSON string)
    val voiceActorsJson: String?, // JSON string of List<VoiceActor>
    
    // Metadata for offline sync
    val lastUpdated: Long = System.currentTimeMillis(),
    val isOfflineAvailable: Boolean = true
)
