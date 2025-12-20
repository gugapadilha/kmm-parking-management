package com.example.gestodeestacionamento.domain.model

data class User(
    val id: Long,
    val email: String,
    val name: String? = null,
    val token: String,
    val establishmentId: Long? = null,
    val sessionId: Long? = null
)

