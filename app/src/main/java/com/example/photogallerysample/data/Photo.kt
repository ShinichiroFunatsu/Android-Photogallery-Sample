package com.example.photogallerysample.data

import android.net.Uri

data class Photo(
    val id: Long,
    val contentUri: Uri,
    val date: Long,
    val mimeType: String? = null
)
