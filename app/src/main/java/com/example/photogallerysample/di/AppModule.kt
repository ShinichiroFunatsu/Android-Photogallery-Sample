package com.example.photogallerysample.di

import com.example.photogallerysample.data.PhotoRepository
import com.example.photogallerysample.data.PhotoRepositoryImpl
import com.example.photogallerysample.viewmodel.GalleryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<PhotoRepository> { PhotoRepositoryImpl() }
}

val uiModule = module {
    viewModel { GalleryViewModel(get()) }
}
