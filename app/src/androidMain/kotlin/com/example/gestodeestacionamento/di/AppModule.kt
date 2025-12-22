package com.example.gestodeestacionamento.di

import android.content.Context
import androidx.room.Room
import com.example.gestodeestacionamento.data.local.AppDatabase
import com.example.gestodeestacionamento.platform.getApplicationContext
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidAppModule = module {
    // Database (Android - Room)
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "parking_database"
        ).build()
    }
    
    // Context para PlatformStorage
    single<Context> { androidContext() }
}

