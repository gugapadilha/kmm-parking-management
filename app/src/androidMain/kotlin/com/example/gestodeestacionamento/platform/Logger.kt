package com.example.gestodeestacionamento.platform

import android.util.Log

actual object PlatformLogger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    
    actual fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
    
    actual fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}

