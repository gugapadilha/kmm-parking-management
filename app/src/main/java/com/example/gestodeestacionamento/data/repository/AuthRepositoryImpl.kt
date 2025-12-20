package com.example.gestodeestacionamento.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gestodeestacionamento.data.mapper.*
import com.example.gestodeestacionamento.data.remote.ApiService
import com.example.gestodeestacionamento.data.remote.dto.LoginResponse
import com.example.gestodeestacionamento.domain.model.User
import com.example.gestodeestacionamento.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val context: Context
) : AuthRepository {

    private val userKey = stringPreferencesKey("user")
    private val tokenKey = stringPreferencesKey("token")
    private val userIdKey = longPreferencesKey("userId")
    private val establishmentIdKey = longPreferencesKey("establishmentId")
    private val sessionIdKey = longPreferencesKey("sessionId")

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
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = user.token
            preferences[userIdKey] = user.id
            user.establishmentId?.let {
                preferences[establishmentIdKey] = it
            }
            user.sessionId?.let {
                preferences[sessionIdKey] = it
            }
        }
    }

    override suspend fun getCurrentUser(): User? {
        val prefs = context.dataStore.data.first()
        val token = prefs[tokenKey] ?: return null
        val userId = prefs[userIdKey] ?: return null
        
        return User(
            id = userId,
            email = "",
            token = token,
            establishmentId = prefs[establishmentIdKey],
            sessionId = prefs[sessionIdKey]
        )
    }

    override suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun saveSessionId(sessionId: Long) {
        context.dataStore.edit { preferences ->
            preferences[sessionIdKey] = sessionId
        }
    }

    override suspend fun getSessionId(): Long? {
        return context.dataStore.data.first()[sessionIdKey]
    }
}

