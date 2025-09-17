package com.dev.jikan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dev.jikan.data.local.converter.Converters

@Entity(tableName = "anime")
@TypeConverters(Converters::class)
data class AnimeEntity(
    @PrimaryKey
    val malId: Int,
    val title: String,
    val titleEnglish: String?,
    val titleJapanese: String?,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val status: String?,
    val airing: Boolean,
    val duration: String?,
    val rating: String?,
    val score: Double?,
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val members: Int?,
    val favorites: Int?,
    val synopsis: String?,
    val background: String?,
    val season: String?,
    val year: Int?,

    // Image URLs (flattened from nested structure)
    val imageUrl: String?,
    val smallImageUrl: String?,
    val largeImageUrl: String?,

    // Trailer info (flattened)
    val trailerYoutubeId: String?,
    val trailerUrl: String?,
    val trailerEmbedUrl: String?,

    // Related entities (stored as JSON strings)
    val genresJson: String?, // JSON string of List<Genre>
    val studiosJson: String?, // JSON string of List<Studio>
    val producersJson: String?, // JSON string of List<Producer>

    // Metadata for offline sync
    val lastUpdated: Long = System.currentTimeMillis(),
    val isOfflineAvailable: Boolean = true
)
