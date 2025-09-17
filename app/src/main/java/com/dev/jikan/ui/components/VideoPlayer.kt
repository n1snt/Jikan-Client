package com.dev.jikan.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.src.main.java.com.dev.jikan.ui_components.components.Icon
import app.src.main.java.com.dev.jikan.ui_components.components.Text
import app.src.main.java.com.dev.jikan.ui_components.components.card.Card
import app.src.main.java.com.dev.jikan.ui_components.components.card.CardDefaults
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev.jikan.data.model.AnimeTrailer

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AnimeTrailerPlayer(
    trailer: AnimeTrailer?,
    posterImageUrl: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Anime Poster Image
        if (!ImageFlags.shouldHideTrailerThumbnail()) {
            GlideImage(
                model = posterImageUrl,
                contentDescription = "Anime poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            // Pretty fallback when images are hidden due to legal constraints
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.error,
                                app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.primary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Trailer",
                        modifier = Modifier.size(64.dp),
                        tint = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onError
                    )
                    Text(
                        text = "Trailer Restricted",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h2,
                        color = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onError
                    )
                    Text(
                        text = "Due to legal constraints",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.body1,
                        color = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onError.copy(
                            alpha = 0.8f
                        )
                    )
                }
            }
        }

        // Small Watch Trailer Button in Bottom Center
        if (trailer?.youtubeId != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    onClick = {
                        // Open YouTube trailer in browser or YouTube app
                        val youtubeUrl = "https://www.youtube.com/watch?v=${trailer.youtubeId}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play trailer",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Watch Trailer",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrailerPlaceholder(
    posterImageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (!ImageFlags.shouldHideTrailerThumbnail()) {
            GlideImage(
                model = posterImageUrl,
                contentDescription = "Anime poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Pretty fallback when images are hidden due to legal constraints
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.surface,
                                app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Poster",
                        modifier = Modifier.size(48.dp),
                        tint = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onSurface
                    )
                    Text(
                        text = "Poster Restricted",
                        style = app.src.main.java.com.dev.jikan.ui_components.AppTheme.typography.h3,
                        color = app.src.main.java.com.dev.jikan.ui_components.AppTheme.colors.onSurface
                    )
                }
            }
        }

        // No trailer available overlay
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = "No Trailer Available",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
