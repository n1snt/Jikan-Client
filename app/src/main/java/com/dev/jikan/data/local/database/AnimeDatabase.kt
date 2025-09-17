package com.dev.jikan.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dev.jikan.data.local.converter.Converters
import com.dev.jikan.data.local.dao.AnimeDao
import com.dev.jikan.data.local.dao.CharacterDao
import com.dev.jikan.data.local.entity.AnimeEntity
import com.dev.jikan.data.local.entity.CharacterEntity

@Database(
    entities = [AnimeEntity::class, CharacterEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun animeDao(): AnimeDao
    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile
        private var INSTANCE: AnimeDatabase? = null

        fun getDatabase(context: Context): AnimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "anime_database"
                )
                .fallbackToDestructiveMigration() // For development - remove in production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
