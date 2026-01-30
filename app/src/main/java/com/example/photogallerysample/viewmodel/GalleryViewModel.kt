package com.example.photogallerysample.viewmodel

import androidx.lifecycle.ViewModel
import com.example.photogallerysample.data.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface GalleryUiState {
    object Initial : GalleryUiState
    object NoPermission : GalleryUiState
    object Empty : GalleryUiState
    data class Content(val albums: List<String>) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}

class GalleryViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Initial)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    fun onPermissionGranted() {
        // Dummy load for now
        _uiState.value = GalleryUiState.Content(listOf("Album A", "Album B"))
        // To test Empty state:
        // _uiState.value = GalleryUiState.Empty
    }

    fun onPermissionDenied() {
        _uiState.value = GalleryUiState.NoPermission
    }
}
