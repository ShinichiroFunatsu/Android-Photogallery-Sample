package com.example.photogallerysample.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photogallerysample.ui.gallery.GalleryScreen
import com.example.photogallerysample.ui.viewer.ViewerScreen

@Composable
fun PhotoGalleryNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = GalleryRoute
    ) {
        composable<GalleryRoute> {
            GalleryScreen(
                onNavigateToViewer = {
                    navController.navigate(ViewerRoute)
                }
            )
        }
        composable<ViewerRoute> {
            ViewerScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
