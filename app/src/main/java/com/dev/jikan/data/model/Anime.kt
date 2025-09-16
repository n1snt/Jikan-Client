package com.dev.jikan.data.model

import com.google.gson.annotations.SerializedName

data class Anime(
    @SerializedName("mal_id")
    val malId: Int,
    val title: String,
    @SerializedName("title_english")
    val titleEnglish: String?,
    @SerializedName("title_japanese")
    val titleJapanese: String?,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val status: String?,
    val airing: Boolean,
    val duration: String?,
    val rating: String?,
    val score: Double?,
    @SerializedName("scored_by")
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
    @SerializedName("youtube_id")
    val youtubeId: String?,
    val url: String?,
    @SerializedName("embed_url")
    val embedUrl: String?,
    val images: TrailerImages?
)

data class TrailerImages(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("medium_image_url")
    val mediumImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?,
    @SerializedName("maximum_image_url")
    val maximumImageUrl: String?
)

data class Genre(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Studio(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Producer(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)
