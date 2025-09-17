package com.dev.jikan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import app.src.main.java.com.dev.jikan.ui_components.components.card.Card
import app.src.main.java.com.dev.jikan.ui_components.components.card.CardDefaults
import app.src.main.java.com.dev.jikan.ui_components.components.Icon
import app.src.main.java.com.dev.jikan.ui_components.AppTheme
import app.src.main.java.com.dev.jikan.ui_components.components.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dev.jikan.data.network.NetworkMonitor

@Composable
fun NetworkStatusIndicator(
    networkMonitor: NetworkMonitor,
    modifier: Modifier = Modifier
) {
    val isOnline by networkMonitor.networkState().collectAsState(initial = true)

    if (!isOnline) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.error
            )
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
                    tint = AppTheme.colors.onError,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You're offline. Showing cached data.",
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.onError
                )
            }
        }
    }
}
