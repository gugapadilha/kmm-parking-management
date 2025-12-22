package com.example.gestodeestacionamento.platform

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

actual class PlatformStorage {
    private val context: Context = getApplicationContext()
    
    actual suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
    
    actual suspend fun getString(key: String): String? {
        return context.dataStore.data.first()[stringPreferencesKey(key)]
    }
    
    actual suspend fun saveLong(key: String, value: Long) {
        context.dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }
    
    actual suspend fun getLong(key: String): Long? {
        return context.dataStore.data.first()[longPreferencesKey(key)]
    }
    
    actual suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

fun getApplicationContext(): Context {
    return org.koin.core.context.GlobalContext.get().get<Context>()
}

