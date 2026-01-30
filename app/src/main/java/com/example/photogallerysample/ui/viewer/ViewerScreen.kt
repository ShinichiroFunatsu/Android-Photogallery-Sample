package com.example.photogallerysample.ui.viewer

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photogallerysample.ui.gallery.EmptyContent
import com.example.photogallerysample.viewmodel.GalleryViewModel
import com.example.photogallerysample.viewmodel.PhotosUiState
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun ViewerScreen(
    bucketId: String,
    initialIndex: Int,
    onBack: () -> Unit,
    viewModel: GalleryViewModel = koinViewModel()
) {
    LaunchedEffect(bucketId) {
        viewModel.loadPhotos(bucketId)
    }

    val state by viewModel.photosUiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (val pState = state) {
            is PhotosUiState.Success -> {
                val pagerState = rememberPagerState(
                    initialPage = initialIndex,
                    pageCount = { pState.photos.size }
                )
                
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val photo = pState.photos[page]
                    val configuration = LocalConfiguration.current
                    val contentScale = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                         ContentScale.FillHeight
                    } else {
                         ContentScale.FillWidth
                    }

                    Box(
                         modifier = Modifier.fillMaxSize(),
                         contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photo.contentUri)
                                .size(2400) // Large enough for viewer
                                .build(),
                            contentDescription = null,
                            contentScale = contentScale,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            PhotosUiState.Initial, PhotosUiState.Loading -> {
                 CircularProgressIndicator(
                     color = Color.White,
                     modifier = Modifier.align(Alignment.Center)
                 )
            }
            is PhotosUiState.Error -> {
                 EmptyContent(message = "Error: ${pState.message}")
            }
        }
        
        // X Button
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
                .size(48.dp) // Larger touch target
                .clickable(onClick = onBack)
                .padding(8.dp) // Inner padding
        )
    }
}
