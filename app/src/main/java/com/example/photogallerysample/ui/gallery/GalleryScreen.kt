package com.example.photogallerysample.ui.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.photogallerysample.viewmodel.GalleryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryScreen(
    onNavigateToViewer: () -> Unit,
    viewModel: GalleryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Gallery Screen")
            Text(text = "Albums: ${uiState.albums}")
            Button(onClick = onNavigateToViewer) {
                Text(text = "Go to Viewer")
            }
        }
    }
}
