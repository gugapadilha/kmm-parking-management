package com.example.gestodeestacionamento.platform

import io.ktor.client.engine.js.Js

actual fun createHttpClientEngine() = Js.create()

