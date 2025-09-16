package com.dev.jikan.data.model

import com.google.gson.annotations.SerializedName

data class Anime(
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
    val images: AnimeImages?,
    val trailer: AnimeTrailer?,
    val genres: List<Genre>?,
    val studios: List<Studio>?,
    val producers: List<Producer>?
)

data class AnimeImages(
    val jpg: AnimeImageUrls?,
    val webp: AnimeImageUrls?
)

data class AnimeImageUrls(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?
)

data class AnimeTrailer(
    val youtubeId: String?,
    val url: String?,
    @SerializedName("embed_url")
    val embedUrl: String?
)

data class Genre(
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Studio(
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Producer(
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)
