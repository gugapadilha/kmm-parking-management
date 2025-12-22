package com.example.gestodeestacionamento.data.repository

import com.example.gestodeestacionamento.data.remote.ApiService
import com.example.gestodeestacionamento.data.remote.dto.LoginResponse
import com.example.gestodeestacionamento.domain.model.User
import com.example.gestodeestacionamento.domain.repository.AuthRepository
import com.example.gestodeestacionamento.platform.PlatformStorage

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val storage: PlatformStorage
) : AuthRepository {

    private val tokenKey = "token"
    private val userIdKey = "userId"
    private val establishmentIdKey = "establishmentId"
    private val sessionIdKey = "sessionId"

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val responseResult = apiService.login(email, password)
            responseResult.fold(
                onSuccess = { response ->
                    val userData = response.data?.user
                    val sessionData = response.data?.session
                    
                    if (userData == null || userData.accessToken == null) {
                        return@fold Result.failure(Exception("Token não recebido"))
                    }
                    
                    // Pegar o primeiro estabelecimento da lista ou do session
                    val establishmentId = sessionData?.establishmentId 
                        ?: userData.establishments?.firstOrNull()
                        ?: response.data?.establishments?.firstOrNull()?.establishmentId
                    
                    val user = User(
                        id = userData.userId ?: 0L,
                        email = userData.email ?: email,
                        name = userData.name,
                        token = userData.accessToken,
                        establishmentId = establishmentId,
                        sessionId = sessionData?.sessionId
                    )
                    saveUser(user)
                    Result.success(user)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUser(user: User) {
        storage.saveString(tokenKey, user.token)
        storage.saveLong(userIdKey, user.id)
        user.establishmentId?.let {
            storage.saveLong(establishmentIdKey, it)
        }
        user.sessionId?.let {
            storage.saveLong(sessionIdKey, it)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val token = storage.getString(tokenKey) ?: return null
        val userId = storage.getLong(userIdKey) ?: return null
        
        return User(
            id = userId,
            email = "",
            token = token,
            establishmentId = storage.getLong(establishmentIdKey),
            sessionId = storage.getLong(sessionIdKey)
        )
    }

    override suspend fun logout() {
        storage.clear()
    }

    override suspend fun saveSessionId(sessionId: Long) {
        storage.saveLong(sessionIdKey, sessionId)
    }

    override suspend fun getSessionId(): Long? {
        return storage.getLong(sessionIdKey)
    }
}

