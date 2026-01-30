package com.example.photogallerysample.data

interface PhotoRepository {
    suspend fun getAlbums(): List<String> // Dummy return type for now
}

class PhotoRepositoryImpl : PhotoRepository {
    override suspend fun getAlbums(): List<String> {
        return listOf("Album 1", "Album 2") // Dummy data
    }
}
