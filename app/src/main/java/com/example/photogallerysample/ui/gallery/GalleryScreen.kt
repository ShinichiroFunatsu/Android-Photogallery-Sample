package com.example.photogallerysample.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.example.photogallerysample.data.Album
import com.example.photogallerysample.viewmodel.GalleryUiState
import com.example.photogallerysample.viewmodel.GalleryViewModel
import com.example.photogallerysample.viewmodel.PhotosUiState
import org.koin.androidx.compose.koinViewModel

import androidx.navigation.toRoute
import com.example.photogallerysample.navigation.AlbumListRoute
import com.example.photogallerysample.navigation.PhotosGridRoute

@Composable
fun GalleryScreen(
    onNavigateToViewer: (String, Int) -> Unit,
    viewModel: GalleryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            GalleryUiState.Initial -> {
                CircularProgressIndicator()
            }
            GalleryUiState.NoPermission -> {
                NoPermissionContent(
                    onRequestPermission = { launcher.launch(permissionName) }
                )
            }
            GalleryUiState.Empty -> {
                EmptyContent(message = "見える写真がありません（選択した写真のみ許可の可能性があります）")
            }
            is GalleryUiState.Error -> {
                EmptyContent(message = "Error: ${state.message}")
            }
            is GalleryUiState.Content -> {
                GalleryShell(
                    albums = state.albums,
                    onNavigateToViewer = onNavigateToViewer,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun GalleryShell(
    albums: List<Album>,
    onNavigateToViewer: (String, Int) -> Unit,
    viewModel: GalleryViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Gallery") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        bottomBar = {
            // Placeholder BottomBar
            BottomNavigation {
                // Fixed dummy items
                BottomNavigationItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        // Dummy icon
                        Text("A")
                    },
                    label = { Text("Albums") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AlbumListRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<AlbumListRoute>(
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            ) {
                AlbumList(
                    albums = albums,
                    onAlbumClick = { bucketId ->
                        navController.navigate(PhotosGridRoute(bucketId))
                    }
                )
            }

            composable<PhotosGridRoute>(
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                }
            ) { backStackEntry ->
                val route = backStackEntry.toRoute<PhotosGridRoute>()
                val bucketId = route.bucketId
                
                LaunchedEffect(bucketId) {
                    viewModel.loadPhotos(bucketId)
                }

                val photosState by viewModel.photosUiState.collectAsState()
                
                when(val pState = photosState) {
                    is PhotosUiState.Success -> {
                        PhotosGridScreen(
                            photos = pState.photos,
                            onPhotoClick = { index ->
                                // Call onNavigateToViewer (Screen3 task)
                                onNavigateToViewer(bucketId, index)
                            },
                        )
                    }
                    PhotosUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is PhotosUiState.Error -> {
                         EmptyContent(message = "Error: ${pState.message}")
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun AlbumList(
    albums: List<Album>,
    onAlbumClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(albums.size) { index ->
            val album = albums[index]
            AlbumItem(album = album, onClick = { onAlbumClick(album.bucketId) })
        }
    }
}

@Composable
fun AlbumItem(
    album: Album,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.coverUri)
                .size(160) // Specify size as per spec
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = album.bucketDisplayName,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${album.count} Photos",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun NoPermissionContent(
    onRequestPermission: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "このアプリを使用するには写真へのアクセス権限が必要です。",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text(text = "権限を許可する")
        }
    }
}

@Composable
internal fun EmptyContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
    }
}

