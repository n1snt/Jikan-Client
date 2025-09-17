package com.dev.jikan.data.model

import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    val data: List<CharacterData>
)

data class CharacterData(
    val character: Character,
    val role: String,
    @SerializedName("voice_actors")
    val voiceActors: List<VoiceActor>
)

data class Character(
    @SerializedName("mal_id")
    val malId: Int,
    val url: String,
    val images: CharacterImages?,
    val name: String
)

data class CharacterImages(
    val jpg: CharacterImageUrls?,
    val webp: CharacterImageUrls?
)

data class CharacterImageUrls(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?
)

data class VoiceActor(
    val person: Person,
    val language: String
)

data class Person(
    @SerializedName("mal_id")
    val malId: Int,
    val url: String,
    val images: PersonImages?,
    val name: String
)

data class PersonImages(
    val jpg: PersonImageUrls?,
    val webp: PersonImageUrls?
)

data class PersonImageUrls(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?
)
