package com.example.photogallerysample.data

import android.net.Uri

data class Album(
    val bucketId: String,
    val bucketDisplayName: String,
    val count: Int,
    val coverUri: Uri,
    val coverDate: Long
)
