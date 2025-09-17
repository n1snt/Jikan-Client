package com.dev.jikan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev.jikan.data.model.CharacterData
import com.dev.jikan.data.model.VoiceActor
import com.dev.jikan.ui.components.ImageFlags
import app.src.main.java.com.dev.jikan.ui_components.components.Text
import app.src.main.java.com.dev.jikan.ui_components.components.card.Card
import app.src.main.java.com.dev.jikan.ui_components.components.Icon

@Composable
fun MainCastSection(
    characters: List<CharacterData>,
    modifier: Modifier = Modifier
) {
    if (characters.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "Main Cast",
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(characters) { characterData ->
                    CharacterCard(characterData = characterData)
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CharacterCard(
    characterData: CharacterData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .height(200.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Character Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = characterData.character.images?.jpg?.imageUrl
                if (!ImageFlags.shouldHideCharacterImage() && imageUrl != null) {
                    GlideImage(
                        model = imageUrl,
                        contentDescription = characterData.character.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Pretty fallback when character image is hidden due to legal constraints
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                    colors = listOf(
                                        app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.tertiary,
                                        app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.primary
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Character",
                                modifier = Modifier.size(32.dp),
                                tint = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onTertiary
                            )
                            Text(
                                text = characterData.character.name.take(1).uppercase(),
                                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h4,
                                color = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onTertiary
                            )
                        }
                    }
                }
            }

            // Character Name
            Text(
                text = characterData.character.name,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            // Voice Actors
            if (characterData.voiceActors.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    characterData.voiceActors.take(2).forEach { voiceActor ->
                        VoiceActorInfo(voiceActor = voiceActor)
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceActorInfo(
    voiceActor: VoiceActor,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Language flag/indicator
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                    when (voiceActor.language.lowercase()) {
                        "japanese" -> Color(0xFFE53E3E) // Red for Japanese
                        "english" -> Color(0xFF3182CE) // Blue for English
                        "korean" -> Color(0xFF38A169) // Green for Korean
                        "chinese" -> Color(0xFFD69E2E) // Yellow for Chinese
                        else -> MaterialTheme.colorScheme.primary
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = voiceActor.language.take(1).uppercase(),
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body3,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Voice actor name
        Text(
            text = voiceActor.person.name,
            style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CharacterLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = modifier
    ) {
        items(5) {
            Card(
                modifier = Modifier
                    .width(140.dp)
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Skeleton for character image
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    // Skeleton for character name
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    // Skeleton for voice actor info
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterErrorCard(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Failed to load characters",
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h3,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = error,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
                textAlign = TextAlign.Center
            )

            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
