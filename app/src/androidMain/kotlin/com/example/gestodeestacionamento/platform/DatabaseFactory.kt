package com.example.gestodeestacionamento.platform

import android.content.Context
import androidx.room.Room
import com.example.gestodeestacionamento.data.local.AppDatabase
import com.example.gestodeestacionamento.data.local.dao.*
import org.koin.core.context.GlobalContext

actual class DatabaseFactory {
    actual fun createDatabase(): AppDatabase {
        val context: Context = GlobalContext.get().get()
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "parking_database"
        ).build()
    }
}

actual typealias AppDatabase = com.example.gestodeestacionamento.data.local.AppDatabase

fun getApplicationContext(): Context {
    return GlobalContext.get().get<Context>()
}

