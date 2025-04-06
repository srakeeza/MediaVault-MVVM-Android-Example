package com.example.mediavault

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import android.app.Application
import com.example.mediavault.di.appModule

class MediaVaultApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            // Use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger(Level.ERROR)
            // Declare Android context
            androidContext(this@MediaVaultApplication)
            // Declare modules
            modules(appModule)
        }
    }
}