package com.dev.jikan.data.local.converter

import androidx.room.TypeConverter
import com.dev.jikan.data.model.Genre
import com.dev.jikan.data.model.Producer
import com.dev.jikan.data.model.Studio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        return genres?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toGenreList(genresJson: String?): List<Genre>? {
        return genresJson?.let {
            val listType = object : TypeToken<List<Genre>>() {}.type
            gson.fromJson(it, listType)
        }
    }
    
    @TypeConverter
    fun fromStudioList(studios: List<Studio>?): String? {
        return studios?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStudioList(studiosJson: String?): List<Studio>? {
        return studiosJson?.let {
            val listType = object : TypeToken<List<Studio>>() {}.type
            gson.fromJson(it, listType)
        }
    }
    
    @TypeConverter
    fun fromProducerList(producers: List<Producer>?): String? {
        return producers?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toProducerList(producersJson: String?): List<Producer>? {
        return producersJson?.let {
            val listType = object : TypeToken<List<Producer>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}
