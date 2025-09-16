package com.dev.jikan.di

import com.dev.jikan.data.remote.JikanApiService
import com.dev.jikan.data.remote.NetworkModule
import com.dev.jikan.data.repository.AnimeRepository

object DependencyProvider {
    
    fun provideJikanApiService(): JikanApiService {
        return NetworkModule.jikanApiService
    }
    
    fun provideAnimeRepository(): AnimeRepository {
        return AnimeRepository(provideJikanApiService())
    }
}
