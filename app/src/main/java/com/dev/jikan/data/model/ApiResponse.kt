package com.dev.jikan.data.model

import com.google.gson.annotations.SerializedName

data class TopAnimeResponse(
    val data: List<Anime>,
    val pagination: Pagination
)

data class AnimeDetailResponse(
    val data: Anime
)

data class Pagination(
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int,
    @SerializedName("has_next_page")
    val hasNextPage: Boolean,
    @SerializedName("current_page")
    val currentPage: Int,
    val items: PaginationItems
)

data class PaginationItems(
    val count: Int,
    val total: Int,
    @SerializedName("per_page")
    val perPage: Int
)
