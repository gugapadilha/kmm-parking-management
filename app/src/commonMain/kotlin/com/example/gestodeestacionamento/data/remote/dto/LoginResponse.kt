package com.example.gestodeestacionamento.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val response: String? = null,
    val data: LoginDataDto? = null,
    val message: String? = null
)

@Serializable
data class LoginDataDto(
    val user: UserDto? = null,
    val session: SessionDto? = null,
    val establishments: List<EstablishmentDto>? = null
)

@Serializable
data class UserDto(
    val userId: Long? = null,
    val email: String? = null,
    val name: String? = null,
    val accessToken: String? = null,
    val uuid: String? = null,
    val userCode: String? = null,
    val phone: String? = null,
    val document: String? = null,
    val profileId: Int? = null,
    val establishments: List<Long>? = null
)

@Serializable
data class SessionDto(
    val sessionId: Long? = null,
    val establishmentId: Long? = null,
    val userId: Long? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val active: Int? = null,
    val code: String? = null
)

@Serializable
data class EstablishmentDto(
    val establishmentId: Long? = null,
    val establishmentCode: String? = null,
    val establishmentName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val userId: Long? = null,
    val status: Int? = null
)

