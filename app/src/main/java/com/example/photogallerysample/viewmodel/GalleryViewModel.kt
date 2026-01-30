package com.example.photogallerysample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallerysample.data.Album
import com.example.photogallerysample.data.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GalleryUiState {
    object Initial : GalleryUiState
    object NoPermission : GalleryUiState
    object Empty : GalleryUiState
    data class Content(val albums: List<Album>) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}

class GalleryViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Initial)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    fun onPermissionGranted() {
        viewModelScope.launch {
            try {
                val albums = repository.getAlbums()
                if (albums.isEmpty()) {
                    _uiState.value = GalleryUiState.Empty
                } else {
                    _uiState.value = GalleryUiState.Content(albums)
                }
            } catch (e: Exception) {
                _uiState.value = GalleryUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onPermissionDenied() {
        _uiState.value = GalleryUiState.NoPermission
    }
}
