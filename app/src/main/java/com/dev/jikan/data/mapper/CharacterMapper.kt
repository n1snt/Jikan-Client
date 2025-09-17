package com.dev.jikan.data.mapper

import com.dev.jikan.data.local.entity.CharacterEntity
import com.dev.jikan.data.model.CharacterData
import com.dev.jikan.data.model.VoiceActor
import com.google.gson.Gson

object CharacterMapper {
    
    private val gson = Gson()
    
    fun toEntity(characterData: CharacterData, animeMalId: Int): CharacterEntity {
        return CharacterEntity(
            characterMalId = characterData.character.malId,
            animeMalId = animeMalId,
            characterName = characterData.character.name,
            characterUrl = characterData.character.url,
            role = characterData.role,
            
            // Flatten character image URLs
            characterImageUrl = characterData.character.images?.jpg?.imageUrl,
            characterSmallImageUrl = characterData.character.images?.jpg?.smallImageUrl,
            characterLargeImageUrl = characterData.character.images?.jpg?.largeImageUrl,
            
            // Convert voice actors to JSON string
            voiceActorsJson = gson.toJson(characterData.voiceActors),
            
            lastUpdated = System.currentTimeMillis(),
            isOfflineAvailable = true
        )
    }
    
    fun toModel(entity: CharacterEntity): CharacterData {
        return CharacterData(
            character = com.dev.jikan.data.model.Character(
                malId = entity.characterMalId,
                url = entity.characterUrl,
                images = if (entity.characterImageUrl != null || entity.characterSmallImageUrl != null || entity.characterLargeImageUrl != null) {
                    com.dev.jikan.data.model.CharacterImages(
                        jpg = com.dev.jikan.data.model.CharacterImageUrls(
                            imageUrl = entity.characterImageUrl,
                            smallImageUrl = entity.characterSmallImageUrl,
                            largeImageUrl = entity.characterLargeImageUrl
                        ),
                        webp = null
                    )
                } else null,
                name = entity.characterName
            ),
            role = entity.role,
            voiceActors = entity.voiceActorsJson?.let { 
                gson.fromJson(it, Array<VoiceActor>::class.java).toList()
            } ?: emptyList()
        )
    }
    
    fun toEntityList(characterDataList: List<CharacterData>, animeMalId: Int): List<CharacterEntity> {
        return characterDataList.map { toEntity(it, animeMalId) }
    }
    
    fun toModelList(entityList: List<CharacterEntity>): List<CharacterData> {
        return entityList.map { toModel(it) }
    }
}
