package com.app.zee5test.di

import android.util.Log
import com.app.zee5test.BuildConfig
import com.app.zee5test.database.AppDatabase
import com.app.zee5test.repository.ApiClient
import com.app.zee5test.repository.AppRepo
import com.app.zee5test.repository.AppRepoImpl
import com.app.zee5test.viewmodel.SearchFragmentViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<ApiClient> {
        Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()
            .create(ApiClient::class.java)
    }

    single { AppDatabase.getDatabase(androidContext()).appDao() }
}

val searchFragmentModule = module {
    single { SearchFragmentViewModel(get()) }
    single<AppRepo> { AppRepoImpl(get(), get()) }
}

fun provideOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(provideHttpInterceptor())
        .build()

fun provideHttpInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor { message -> Log.d("Okhttp", message) }.also {
        it.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }