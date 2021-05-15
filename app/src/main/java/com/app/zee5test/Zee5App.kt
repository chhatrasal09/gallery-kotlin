package com.app.zee5test

import android.app.Application
import com.app.zee5test.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Zee5App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Zee5App)
            modules(listOf(appModule))
        }
    }
}