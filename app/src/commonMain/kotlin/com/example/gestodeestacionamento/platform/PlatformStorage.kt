package com.example.gestodeestacionamento.platform

expect class PlatformStorage() {
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun saveLong(key: String, value: Long)
    suspend fun getLong(key: String): Long?
    suspend fun clear()
}

