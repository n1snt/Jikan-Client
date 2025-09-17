package com.dev.jikan.data.remote

import com.dev.jikan.data.model.AnimeDetailResponse
import com.dev.jikan.data.model.TopAnimeResponse
import com.dev.jikan.data.model.CharacterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApiService {

    @GET("v4/top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25,
        @Query("type") type: String = "tv",
        @Query("filter") filter: String = "bypopularity"
    ): Response<TopAnimeResponse>

    @GET("v4/anime/{id}")
    suspend fun getAnimeById(@Path("id") id: Int): Response<AnimeDetailResponse>

    @GET("v4/anime/{id}/full")
    suspend fun getAnimeFull(@Path("id") id: Int): Response<AnimeDetailResponse>

    @GET("v4/anime/{id}/characters")
    suspend fun getAnimeCharacters(@Path("id") id: Int): Response<CharacterResponse>
}
