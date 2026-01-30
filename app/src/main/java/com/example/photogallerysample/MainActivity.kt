package com.example.photogallerysample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.photogallerysample.navigation.GalleryRoute
import com.example.photogallerysample.navigation.ViewerRoute
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
                    startDestination = GalleryRoute
                ) {
                    composable<GalleryRoute> {
                         GalleryScreen(
                             onNavigateToViewer = { bucketId, index ->
                                 navController.navigate(ViewerRoute(bucketId, index))
                             }
                         )
                    }
                    
                    composable<ViewerRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<ViewerRoute>()
                        val bucketId = route.bucketId
                        val initialIndex = route.initialIndex
                        
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