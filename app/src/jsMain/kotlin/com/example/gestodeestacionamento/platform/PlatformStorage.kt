package com.example.gestodeestacionamento.platform

actual class PlatformStorage {
    actual suspend fun saveString(key: String, value: String) {
        kotlinx.browser.localStorage.setItem(key, value)
    }
    
    actual suspend fun getString(key: String): String? {
        return kotlinx.browser.localStorage.getItem(key)
    }
    
    actual suspend fun saveLong(key: String, value: Long) {
        kotlinx.browser.localStorage.setItem(key, value.toString())
    }
    
    actual suspend fun getLong(key: String): Long? {
        return kotlinx.browser.localStorage.getItem(key)?.toLongOrNull()
    }
    
    actual suspend fun clear() {
        kotlinx.browser.localStorage.clear()
    }
}

