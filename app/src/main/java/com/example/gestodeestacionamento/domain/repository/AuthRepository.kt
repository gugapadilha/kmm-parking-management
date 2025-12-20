package com.example.gestodeestacionamento.domain.repository

import com.example.gestodeestacionamento.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun saveUser(user: User)
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun saveSessionId(sessionId: Long)
    suspend fun getSessionId(): Long?
}

