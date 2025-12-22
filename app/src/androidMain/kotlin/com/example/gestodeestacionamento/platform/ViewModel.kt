package com.example.gestodeestacionamento.platform

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel as androidKoinViewModel

actual inline fun <reified T : Any> koinViewModel(): T = androidKoinViewModel<T>()

