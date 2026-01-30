package com.example.photogallerysample.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PhotoRepository {
    suspend fun getAlbums(): List<Album>
}

class PhotoRepositoryImpl(
    private val context: Context
) : PhotoRepository {

    override suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableMapOf<String, AlbumBuilder>()

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn) ?: continue
                val bucketName = cursor.getString(bucketNameColumn) ?: clientFallbackName(bucketId)
                val id = cursor.getLong(idColumn)
                // dateTaken fallback logic: prioritizes DATE_TAKEN, then DATE_ADDED
                val dateTaken = cursor.getLong(dateTakenColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                // Use dateTaken if valid (>0), else use dateAdded * 1000 (if seconds) or as is.
                // MediaStore.Images.Media.DATE_ADDED is in seconds, DATE_TAKEN is in millis.
                val timestamp = if (dateTaken > 0) {
                    dateTaken
                } else {
                    dateAdded * 1000
                }

                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                val builder = albums.getOrPut(bucketId) {
                    AlbumBuilder(
                        bucketId = bucketId,
                        bucketDisplayName = bucketName,
                        coverUri = uri,
                        coverDate = timestamp
                    )
                }
                builder.count++
            }
        }
        
        return@withContext albums.values.map {
            Album(
                bucketId = it.bucketId,
                bucketDisplayName = it.bucketDisplayName,
                count = it.count,
                coverUri = it.coverUri,
                coverDate = it.coverDate
            )
        }.sortedByDescending { it.coverDate }
    }

    private fun clientFallbackName(bucketId: String): String {
        return "Unknown"
    }

    private data class AlbumBuilder(
        val bucketId: String,
        val bucketDisplayName: String,
        var count: Int = 0,
        val coverUri: Uri,
        val coverDate: Long
    )
}
