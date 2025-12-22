package com.example.gestodeestacionamento.platform

import io.ktor.client.engine.android.Android

actual fun createHttpClientEngine() = Android.create()

