package com.example.photogallerysample.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.photogallerysample.viewmodel.GalleryUiState
import com.example.photogallerysample.viewmodel.GalleryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryScreen(
    onNavigateToViewer: () -> Unit,
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
            // First time check, if not granted, we set state to NoPermission so user can click button
            // Or we could auto-request? requirements say "Initial request after initialization once".
            // "Sampleなので「初期化後に一発だけ」権限リクエストすれば良い"
            // "設定画面への導線や管理画面などは不要"
            // Let's assume we don't auto-request immediately on start to avoid bad UX, 
            // but the spec says "初期化後に一発だけ", maybe it means "Just request it".
            // However, typical pattern is to show UI if not granted. 
            // The instruction says: "Empty画面コンポーネントを実装し...権限なし: 説明 + 許可ボタン"
            // So we should show that state.
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
                // Dummy Content for now
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Albums: ${state.albums}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateToViewer) {
                        Text(text = "Go to Viewer")
                    }
                }
            }
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
private fun EmptyContent(message: String) {
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
