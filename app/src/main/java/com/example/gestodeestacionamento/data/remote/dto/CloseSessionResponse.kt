package com.example.gestodeestacionamento.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloseSessionResponse(
    val success: Boolean? = null,
    val message: String? = null
)

