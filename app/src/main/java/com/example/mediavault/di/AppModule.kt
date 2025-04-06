package com.example.mediavault.di

import com.example.mediavault.repository.MediaRepository
import com.example.mediavault.viewmodel.MediaViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Single instance of MediaRepository
    single { MediaRepository(androidContext()) }

    // ViewModel
    viewModel { MediaViewModel(get()) }
}
