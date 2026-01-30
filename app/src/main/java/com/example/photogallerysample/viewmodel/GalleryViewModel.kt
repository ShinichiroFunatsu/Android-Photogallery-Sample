package com.example.photogallerysample.viewmodel

import androidx.lifecycle.ViewModel
import com.example.photogallerysample.data.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GalleryUiState(
    val albums: List<String> = emptyList(),
    val isLoading: Boolean = false
)

class GalleryViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        // Dummy initialization to confirm ViewModel is working
        _uiState.value = GalleryUiState(albums = listOf("Initialized from ViewModel"))
    }
}
