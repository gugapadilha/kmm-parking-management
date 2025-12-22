package com.example.gestodeestacionamento.platform

actual object PlatformLogger {
    actual fun d(tag: String, message: String) {
        console.log("[$tag] $message")
    }
    
    actual fun w(tag: String, message: String) {
        console.warn("[$tag] $message")
    }
    
    actual fun e(tag: String, message: String, throwable: Throwable?) {
        console.error("[$tag] $message", throwable)
    }
}

