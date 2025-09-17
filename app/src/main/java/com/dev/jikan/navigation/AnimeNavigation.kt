package com.dev.jikan.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.jikan.ui.screen.AnimeDetailScreen
import com.dev.jikan.ui.screen.AnimeListScreen

@Composable
fun AnimeNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "anime_list"
    ) {
        composable("anime_list") {
            AnimeListScreen(
                onAnimeClick = { animeId ->
                    navController.navigate("anime_detail/$animeId")
                }
            )
        }

        composable("anime_detail/{animeId}") { backStackEntry ->
            val animeIdString = backStackEntry.arguments?.getString("animeId")
            val animeId = animeIdString?.toIntOrNull() ?: 0
            AnimeDetailScreen(
                animeId = animeId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
