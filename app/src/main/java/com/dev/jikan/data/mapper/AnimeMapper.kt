package com.dev.jikan.data.mapper

import com.dev.jikan.data.local.entity.AnimeEntity
import com.dev.jikan.data.model.Anime
import com.google.gson.Gson

object AnimeMapper {
    
    private val gson = Gson()
    
    fun toEntity(anime: Anime): AnimeEntity {
        return AnimeEntity(
            malId = anime.malId,
            title = anime.title,
            titleEnglish = anime.titleEnglish,
            titleJapanese = anime.titleJapanese,
            type = anime.type,
            source = anime.source,
            episodes = anime.episodes,
            status = anime.status,
            airing = anime.airing,
            duration = anime.duration,
            rating = anime.rating,
            score = anime.score,
            scoredBy = anime.scoredBy,
            rank = anime.rank,
            popularity = anime.popularity,
            members = anime.members,
            favorites = anime.favorites,
            synopsis = anime.synopsis,
            background = anime.background,
            season = anime.season,
            year = anime.year,
            
            // Flatten image URLs
            imageUrl = anime.images?.jpg?.imageUrl,
            smallImageUrl = anime.images?.jpg?.smallImageUrl,
            largeImageUrl = anime.images?.jpg?.largeImageUrl,
            
            // Flatten trailer info
            trailerYoutubeId = anime.trailer?.youtubeId,
            trailerUrl = anime.trailer?.url,
            trailerEmbedUrl = anime.trailer?.embedUrl,
            
            // Convert lists to JSON strings
            genresJson = anime.genres?.let { gson.toJson(it) },
            studiosJson = anime.studios?.let { gson.toJson(it) },
            producersJson = anime.producers?.let { gson.toJson(it) },
            
            lastUpdated = System.currentTimeMillis(),
            isOfflineAvailable = true
        )
    }
    
    fun toModel(entity: AnimeEntity): Anime {
        return Anime(
            malId = entity.malId,
            title = entity.title,
            titleEnglish = entity.titleEnglish,
            titleJapanese = entity.titleJapanese,
            type = entity.type,
            source = entity.source,
            episodes = entity.episodes,
            status = entity.status,
            airing = entity.airing,
            duration = entity.duration,
            rating = entity.rating,
            score = entity.score,
            scoredBy = entity.scoredBy,
            rank = entity.rank,
            popularity = entity.popularity,
            members = entity.members,
            favorites = entity.favorites,
            synopsis = entity.synopsis,
            background = entity.background,
            season = entity.season,
            year = entity.year,
            
            // Reconstruct nested image structure
            images = if (entity.imageUrl != null || entity.smallImageUrl != null || entity.largeImageUrl != null) {
                com.dev.jikan.data.model.AnimeImages(
                    jpg = com.dev.jikan.data.model.AnimeImageUrls(
                        imageUrl = entity.imageUrl,
                        smallImageUrl = entity.smallImageUrl,
                        largeImageUrl = entity.largeImageUrl
                    ),
                    webp = null
                )
            } else null,
            
            // Reconstruct trailer structure
            trailer = if (entity.trailerYoutubeId != null || entity.trailerUrl != null || entity.trailerEmbedUrl != null) {
                com.dev.jikan.data.model.AnimeTrailer(
                    youtubeId = entity.trailerYoutubeId,
                    url = entity.trailerUrl,
                    embedUrl = entity.trailerEmbedUrl,
                    images = null
                )
            } else null,
            
            // Parse JSON strings back to lists
            genres = entity.genresJson?.let { 
                gson.fromJson(it, Array<com.dev.jikan.data.model.Genre>::class.java).toList()
            },
            studios = entity.studiosJson?.let { 
                gson.fromJson(it, Array<com.dev.jikan.data.model.Studio>::class.java).toList()
            },
            producers = entity.producersJson?.let { 
                gson.fromJson(it, Array<com.dev.jikan.data.model.Producer>::class.java).toList()
            }
        )
    }
    
    fun toEntityList(animeList: List<Anime>): List<AnimeEntity> {
        return animeList.map { toEntity(it) }
    }
    
    fun toModelList(entityList: List<AnimeEntity>): List<Anime> {
        return entityList.map { toModel(it) }
    }
}
