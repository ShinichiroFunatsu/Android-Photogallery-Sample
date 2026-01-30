package com.example.photogallerysample.navigation

import kotlinx.serialization.Serializable

@Serializable
object GalleryRoute

@Serializable
data class ViewerRoute(
    val bucketId: String,
    val initialIndex: Int
)

// Inner routes for GalleryShell
@Serializable
object AlbumListRoute

@Serializable
data class PhotosGridRoute(
    val bucketId: String
)
