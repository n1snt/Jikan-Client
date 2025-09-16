package com.dev.jikan.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev.jikan.data.model.Anime
import com.dev.jikan.ui.viewmodel.AnimeListViewModel
import app.src.main.java.com.dev.jikan.ui_components.components.Scaffold
import app.src.main.java.com.dev.jikan.ui_components.components.Text
import app.src.main.java.com.dev.jikan.ui_components.components.card.Card
import app.src.main.java.com.dev.jikan.ui_components.components.Button
import app.src.main.java.com.dev.jikan.ui_components.components.topbar.TopBar
import app.src.main.java.com.dev.jikan.ui_components.components.progressindicators.CircularProgressIndicator
import kotlinx.coroutines.launch

@Composable
fun AnimeListScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: AnimeListViewModel = hiltViewModel()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            Box(
                modifier = Modifier.fillMaxSize()
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
                        isLoadingMore = uiState.isLoadingMore,
                        hasNextPage = uiState.hasNextPage,
                        paginationError = uiState.paginationError,
                        onLoadMore = { viewModel.loadMoreAnime() },
                        onRetryLoadMore = { viewModel.retryLoadMore() },
                        onClearPaginationError = { viewModel.clearPaginationError() },
                        onRefresh = { viewModel.refreshAnimeList() }
                    )
                }
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
    isLoadingMore: Boolean,
    hasNextPage: Boolean,
    paginationError: String?,
    onLoadMore: () -> Unit,
    onRetryLoadMore: () -> Unit,
    onClearPaginationError: () -> Unit,
    onRefresh: () -> Unit
) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    // Infinite scroll logic
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= animeList.size - 6 && // Load when 6 items from bottom
                    hasNextPage &&
                    !isLoadingMore) {
                    onLoadMore()
                }
            }
    }

    LazyVerticalGrid(
        state = gridState,
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

        // Loading more indicator
        if (isLoadingMore) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Pagination error
        if (paginationError != null) {
            item(span = { GridItemSpan(2) }) {
                PaginationErrorCard(
                    error = paginationError,
                    onRetry = onRetryLoadMore,
                    onDismiss = onClearPaginationError
                )
            }
        }

        // End of list indicator
        if (!hasNextPage && animeList.isNotEmpty() && !isLoadingMore) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You've reached the end!",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2
                    )
                }
            }
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

@Composable
fun PaginationErrorCard(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Failed to load more anime",
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h3,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onRetry) {
                    Text("Retry")
                }

                Button(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        }
    }
}
