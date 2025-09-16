package com.dev.jikan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev.jikan.data.model.Anime
import com.dev.jikan.di.DependencyProvider
import com.dev.jikan.ui.viewmodel.AnimeDetailViewModel
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
    viewModel: AnimeDetailViewModel = viewModel {
        AnimeDetailViewModel(DependencyProvider.provideAnimeRepository())
    }
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
            // Anime Poster/Image
            GlideImage(
                model = anime.images?.jpg?.largeImageUrl ?: anime.images?.jpg?.imageUrl,
                contentDescription = anime.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
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
            }
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
