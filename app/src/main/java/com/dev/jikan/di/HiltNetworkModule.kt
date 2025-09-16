package com.dev.jikan.di

import android.content.Context
import com.dev.jikan.data.network.NetworkMonitor
import com.dev.jikan.data.remote.JikanApiService
import com.dev.jikan.data.remote.NetworkModule as RemoteNetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltNetworkModule {
    
    @Provides
    @Singleton
    fun provideJikanApiService(): JikanApiService {
        return RemoteNetworkModule.jikanApiService
    }
    
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
}
