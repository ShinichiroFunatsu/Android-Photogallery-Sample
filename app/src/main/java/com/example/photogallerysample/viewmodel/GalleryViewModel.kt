package com.example.photogallerysample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallerysample.data.Album
import com.example.photogallerysample.data.PhotoRepository
import com.example.photogallerysample.data.Photo
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

sealed interface PhotosUiState {
    object Initial : PhotosUiState
    object Loading : PhotosUiState
    data class Success(val photos: List<Photo>) : PhotosUiState
    data class Error(val message: String) : PhotosUiState
}

class GalleryViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Initial)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _photosUiState = MutableStateFlow<PhotosUiState>(PhotosUiState.Initial)
    val photosUiState: StateFlow<PhotosUiState> = _photosUiState.asStateFlow()

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

    fun loadPhotos(bucketId: String) {
        _photosUiState.value = PhotosUiState.Loading
        viewModelScope.launch {
            try {
                val photos = repository.getPhotos(bucketId)
                _photosUiState.value = PhotosUiState.Success(photos)
            } catch (e: Exception) {
                _photosUiState.value = PhotosUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
