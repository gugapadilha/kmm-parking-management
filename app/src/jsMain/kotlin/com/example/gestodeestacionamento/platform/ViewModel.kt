package com.example.gestodeestacionamento.platform

import androidx.compose.runtime.Composable
import org.koin.core.context.GlobalContext

actual inline fun <reified T : Any> koinViewModel(): T {
    return GlobalContext.get().get<T>()
}

