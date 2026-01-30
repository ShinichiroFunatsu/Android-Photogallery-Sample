package com.example.photogallerysample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.photogallerysample.ui.gallery.GalleryScreen
import com.example.photogallerysample.ui.theme.PhotoGallerySampleTheme
import com.example.photogallerysample.ui.viewer.ViewerScreen
import com.example.photogallerysample.viewmodel.GalleryViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoGallerySampleTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "gallery_root"
                ) {
                    composable("gallery_root") {
                         GalleryScreen(
                             onNavigateToViewer = { bucketId, index ->
                                 navController.navigate("viewer_route/$bucketId/$index")
                             }
                         )
                    }
                    
                    composable(
                        route = "viewer_route/{bucketId}/{initialIndex}",
                        arguments = listOf(
                             navArgument("bucketId") { type = NavType.StringType },
                             navArgument("initialIndex") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val bucketId = backStackEntry.arguments?.getString("bucketId") ?: return@composable
                        val initialIndex = backStackEntry.arguments?.getInt("initialIndex") ?: 0
                        
                        // Explicitly get viewModel to ensure Composable context logic is clear
                        val viewModel: GalleryViewModel = koinViewModel()
                        
                        ViewerScreen(
                             bucketId = bucketId,
                             initialIndex = initialIndex,
                             onBack = { navController.popBackStack() },
                             viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}