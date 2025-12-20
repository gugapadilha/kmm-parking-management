package com.example.gestodeestacionamento.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloseSessionRequest(
    val dateTime: String? = null // formato: "2023-02-09 14:03:14"
)

