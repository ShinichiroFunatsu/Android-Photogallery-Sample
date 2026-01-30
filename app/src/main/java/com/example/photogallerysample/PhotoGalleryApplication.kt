package com.example.photogallerysample

import android.app.Application
import com.example.photogallerysample.di.dataModule
import com.example.photogallerysample.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PhotoGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PhotoGalleryApplication)
            modules(dataModule, uiModule)
        }
    }
}
