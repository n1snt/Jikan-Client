package com.dev.jikan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev.jikan.data.model.Anime
import com.dev.jikan.ui.viewmodel.AnimeDetailViewModel
import com.dev.jikan.ui.components.AnimeTrailerPlayer
import com.dev.jikan.ui.components.TrailerPlaceholder
import app.src.main.java.com.dev.jikan.ui_components.components.Scaffold
import app.src.main.java.com.dev.jikan.ui_components.components.Text
import app.src.main.java.com.dev.jikan.ui_components.components.card.Card
import app.src.main.java.com.dev.jikan.ui_components.components.Button
import app.src.main.java.com.dev.jikan.ui_components.components.topbar.TopBar
import app.src.main.java.com.dev.jikan.ui_components.components.progressindicators.CircularProgressIndicator
import app.src.main.java.com.dev.jikan.ui_components.components.Icon

@Composable
fun AnimeDetailScreen(
    animeId: Int,
    onBackClick: () -> Unit,
    viewModel: AnimeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animeId) {
        println("DEBUG: Loading anime details for ID: $animeId")
        if (animeId > 0) {
            viewModel.loadAnimeDetails(animeId)
        }
    }

    Scaffold(
        topBar = {
            TopBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text("Anime Details")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                animeId <= 0 -> {
                    ErrorScreen(
                        error = "Invalid anime ID: $animeId",
                        onRetry = { /* No retry for invalid ID */ }
                    )
                }
                uiState.isLoading -> {
                    LoadingScreen()
                }
                uiState.error != null -> {
                    ErrorScreen(
                        error = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.loadAnimeDetails(animeId) }
                    )
                }
                uiState.anime != null -> {
                    AnimeDetailContent(anime = uiState.anime!!)
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AnimeDetailContent(anime: Anime) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Anime Trailer/Poster
            if (anime.trailer?.youtubeId != null) {
                AnimeTrailerPlayer(
                    trailer = anime.trailer,
                    posterImageUrl = anime.images?.jpg?.largeImageUrl ?: anime.images?.jpg?.imageUrl
                )
            } else {
                TrailerPlaceholder(
                    posterImageUrl = anime.images?.jpg?.largeImageUrl ?: anime.images?.jpg?.imageUrl
                )
            }
        }

        item {
            // Title and Basic Info
            Column {
                Text(
                    text = anime.title,
                    style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h1,
                    fontWeight = FontWeight.Bold
                )

                if (!anime.titleEnglish.isNullOrEmpty() && anime.titleEnglish != anime.title) {
                    Text(
                        text = anime.titleEnglish,
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h3
                    )
                }

                if (!anime.titleJapanese.isNullOrEmpty() && anime.titleJapanese != anime.title) {
                    Text(
                        text = anime.titleJapanese,
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h4
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating and Episodes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (anime.score != null) {
                        Card {
                            Text(
                                text = "★ ${String.format("%.1f", anime.score)}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (anime.episodes != null) {
                        Card {
                            Text(
                                text = "${anime.episodes} Episodes",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            // Synopsis
            if (!anime.synopsis.isNullOrEmpty()) {
                Column {
                    Text(
                        text = "Synopsis",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = anime.synopsis,
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body1
                    )
                }
            }
        }

        item {
            // Background Information
            if (!anime.background.isNullOrEmpty()) {
                Column {
                    Text(
                        text = "Background",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = anime.background,
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body1
                    )
                }
            }
        }

        item {
            // Genres
            if (!anime.genres.isNullOrEmpty()) {
                Column {
                    Text(
                        text = "Genres",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        anime.genres.take(5).forEach { genre ->
                            Card {
                                Text(
                                    text = genre.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Studios
            if (!anime.studios.isNullOrEmpty()) {
                Column {
                    Text(
                        text = "Studios",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        anime.studios.forEach { studio ->
                            Card {
                                Text(
                                    text = studio.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Producers
            if (!anime.producers.isNullOrEmpty()) {
                Column {
                    Text(
                        text = "Producers",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(anime.producers.take(8)) { producer ->
                            ProducerChip(producer.name)
                        }
                    }
                }
            }
        }

        item {
            // Statistics
            Column {
                Text(
                    text = "Statistics",
                    style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Statistics Grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // First Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (anime.score != null) {
                            EnhancedStatCard(
                                icon = Icons.Default.Star,
                                label = "Score",
                                value = String.format("%.1f", anime.score),
                                subtitle = "${anime.scoredBy ?: 0} users",
                                color = androidx.compose.ui.graphics.Color(0xFFFFD700), // Gold
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (anime.members != null) {
                            EnhancedStatCard(
                                icon = Icons.Default.Person,
                                label = "Members",
                                value = formatNumber(anime.members),
                                subtitle = "Total members",
                                color = androidx.compose.ui.graphics.Color(0xFF4CAF50), // Green
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Second Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (anime.popularity != null) {
                            EnhancedStatCard(
                                icon = Icons.Default.Info,
                                label = "Popularity",
                                value = "#${anime.popularity}",
                                subtitle = "Most popular",
                                color = androidx.compose.ui.graphics.Color(0xFF2196F3), // Blue
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (anime.favorites != null) {
                            EnhancedStatCard(
                                icon = Icons.Default.Favorite,
                                label = "Favorites",
                                value = formatNumber(anime.favorites),
                                subtitle = "User favorites",
                                color = androidx.compose.ui.graphics.Color(0xFFE91E63), // Pink
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        item {
            // Additional Info
            Column {
                Text(
                    text = "Additional Information",
                    style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                InfoRow("Type", anime.type ?: "N/A")
                InfoRow("Status", anime.status ?: "N/A")
                InfoRow("Source", anime.source ?: "N/A")
                InfoRow("Duration", anime.duration ?: "N/A")
                InfoRow("Rating", anime.rating ?: "N/A")
                if (anime.year != null) {
                    InfoRow("Year", anime.year.toString())
                }
                if (anime.season != null) {
                    InfoRow("Season", anime.season)
                }
                if (anime.rank != null) {
                    InfoRow("Rank", "#${anime.rank}")
                }
                InfoRow("Airing", if (anime.airing) "Currently Airing" else "Not Airing")
            }
        }
    }
}


@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subtitle: String
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h3,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProducerChip(producerName: String) {
    Card(
        modifier = Modifier.padding(vertical = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = producerName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EnhancedStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(40.dp),
                    tint = color
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}K"
        else -> number.toString()
    }
}
