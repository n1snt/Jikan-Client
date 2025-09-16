package com.dev.jikan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = { Text("Anime Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                if (!anime.titleEnglish.isNullOrEmpty() && anime.titleEnglish != anime.title) {
                    Text(
                        text = anime.titleEnglish,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating and Episodes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (anime.score != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "★ ${String.format("%.1f", anime.score)}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    if (anime.episodes != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "${anime.episodes} Episodes",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = anime.synopsis,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        anime.genres.take(5).forEach { genre ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {
                                Text(
                                    text = genre.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        anime.studios.forEach { studio ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = studio.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    style = MaterialTheme.typography.titleLarge,
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
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
