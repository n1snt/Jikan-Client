package com.dev.jikan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev.jikan.data.model.Anime
import com.dev.jikan.di.DependencyProvider
import com.dev.jikan.ui.viewmodel.AnimeListViewModel
import app.src.main.java.com.dev.jikan.ui_components.components.Scaffold
import app.src.main.java.com.dev.jikan.ui_components.components.Text
import app.src.main.java.com.dev.jikan.ui_components.components.card.Card
import app.src.main.java.com.dev.jikan.ui_components.components.Button
import app.src.main.java.com.dev.jikan.ui_components.components.topbar.TopBar
import app.src.main.java.com.dev.jikan.ui_components.components.progressindicators.CircularProgressIndicator

@Composable
fun AnimeListScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: AnimeListViewModel = viewModel {
        AnimeListViewModel(DependencyProvider.provideAnimeRepository())
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAnimeList()
    }

    Scaffold(
        topBar = {
            TopBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Top Anime")
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
                uiState.isLoading && uiState.animeList.isEmpty() -> {
                    LoadingScreen()
                }
                uiState.error != null && uiState.animeList.isEmpty() -> {
                    ErrorScreen(
                        error = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.loadAnimeList() }
                    )
                }
                else -> {
                    AnimeGrid(
                        animeList = uiState.animeList,
                        onAnimeClick = onAnimeClick,
                        isLoading = uiState.isLoading,
                        onRefresh = { viewModel.refreshAnimeList() }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
                    Text(
                        text = "Error: $error",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body1
                    )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun AnimeGrid(
    animeList: List<Anime>,
    onAnimeClick: (Int) -> Unit,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(animeList) { anime ->
            AnimeCard(
                anime = anime,
                onClick = {
                    println("DEBUG: Anime clicked - Title: '${anime.title}', malId: ${anime.malId}")
                    onAnimeClick(anime.malId)
                }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AnimeCard(
    anime: Anime,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        onClick = onClick
    ) {
        Column {
            // Anime Poster
            GlideImage(
                model = anime.images?.jpg?.imageUrl,
                contentDescription = anime.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Anime Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = anime.title,
                    style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Episodes: ${anime.episodes ?: "N/A"}",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body3
                    )

                    if (anime.score != null) {
                        Text(
                            text = "★ ${String.format("%.1f", anime.score)}",
                            style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body3,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
