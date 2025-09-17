package com.dev.jikan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import app.src.main.java.com.dev.jikan.ui_components.components.Icon
import app.src.main.java.com.dev.jikan.ui_components.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.src.main.java.com.dev.jikan.ui_components.components.Button
import app.src.main.java.com.dev.jikan.ui_components.components.Scaffold
import app.src.main.java.com.dev.jikan.ui_components.components.Text
import app.src.main.java.com.dev.jikan.ui_components.components.card.Card
import app.src.main.java.com.dev.jikan.ui_components.components.progressindicators.CircularProgressIndicator
import app.src.main.java.com.dev.jikan.ui_components.components.topbar.TopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev.jikan.data.model.Anime
import com.dev.jikan.ui.components.ImageFlags
import com.dev.jikan.ui.viewmodel.AnimeListViewModel

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
            // Network status indicator
            val isOnline by viewModel.networkState.collectAsState(initial = true)
            if (!isOnline) {
                OfflineIndicator(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

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
            if (!ImageFlags.shouldHideAnimePoster()) {
                GlideImage(
                    model = anime.images?.jpg?.imageUrl,
                    contentDescription = anime.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Pretty fallback when images are hidden due to legal constraints
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.tertiary,
                                    app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Anime icon
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Anime",
                            modifier = Modifier.size(48.dp),
                            tint = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onTertiary
                        )

                        // Anime title
                        Text(
                            text = anime.title,
                            style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h3,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            color = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onTertiary
                        )

                        // Rating if available
                        if (anime.score != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    modifier = Modifier.size(16.dp),
                                    tint = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onTertiary
                                )
                                Text(
                                    text = String.format("%.1f", anime.score),
                                    style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body2,
                                    color = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onTertiary
                                )
                            }
                        }
                    }
                }
            }

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

@Composable
fun OfflineIndicator(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Offline",
                tint = AppTheme.colors.error,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "You're offline. Showing cached data.",
                style = AppTheme.typography.body2,
                color = AppTheme.colors.error
            )
        }
    }
}
