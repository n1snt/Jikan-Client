package com.dev.jikan.di

import android.content.Context
import com.dev.jikan.data.local.dao.AnimeDao
import com.dev.jikan.data.local.dao.CharacterDao
import com.dev.jikan.data.local.database.AnimeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAnimeDatabase(@ApplicationContext context: Context): AnimeDatabase {
        return AnimeDatabase.getDatabase(context)
    }

    @Provides
    fun provideAnimeDao(database: AnimeDatabase): AnimeDao {
        return database.animeDao()
    }

    @Provides
    fun provideCharacterDao(database: AnimeDatabase): CharacterDao {
        return database.characterDao()
    }
}
