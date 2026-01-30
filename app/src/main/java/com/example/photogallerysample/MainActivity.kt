package com.example.photogallerysample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.photogallerysample.navigation.PhotoGalleryNavHost
import com.example.photogallerysample.ui.theme.PhotoGallerySampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoGallerySampleTheme {
                PhotoGalleryNavHost()
            }
        }
    }
}