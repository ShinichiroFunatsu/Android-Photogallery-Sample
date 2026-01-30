package com.example.photogallerysample.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe route definitions for the app.
 * The hierarchy is explicitly defined using nested types.
 */
sealed interface Routes {

    /**
     * Parent route for the gallery feature.
     * Contains inner routes for album listing and photo grid.
     */
    @Serializable
    object Gallery : Routes {

        /**
         * Route for displaying the list of albums.
         */
        @Serializable
        object AlbumList

        /**
         * Route for displaying photos within a specific album.
         * @param bucketId The ID of the album (bucket) to display.
         */
        @Serializable
        data class PhotosGrid(
            val bucketId: String
        )
    }

    /**
     * Independent route for the full-screen photo viewer.
     * @param bucketId The ID of the album being viewed.
     * @param initialIndex The index of the photo to show initially.
     */
    @Serializable
    data class Viewer(
        val bucketId: String,
        val initialIndex: Int
    ) : Routes
}
