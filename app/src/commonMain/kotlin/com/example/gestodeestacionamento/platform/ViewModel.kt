package com.example.gestodeestacionamento.platform

import androidx.compose.runtime.Composable

expect inline fun <reified T : Any> koinViewModel(): T

