package com.dev.jikan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import app.src.main.java.com.dev.jikan.ui_components.AppTheme

@Composable
fun JikanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    AppTheme(
        isDarkTheme = darkTheme,
        content = content
    )
}
