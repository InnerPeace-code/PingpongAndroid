package com.pingpong.app.core.data

import com.pingpong.app.core.auth.TokenManager
import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.model.ApiResponse
import com.pingpong.app.core.model.LoginRequest
import com.pingpong.app.core.model.LoginResponse
import com.pingpong.app.core.model.TokenInfo
import com.pingpong.app.core.network.api.AuthApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun login(request: LoginRequest): ApiResponse<LoginResponse> = withContext(ioDispatcher) {
        val response = when (request.role) {
            "super_admin" -> authApi.superAdminLogin(request)
            "campus_admin" -> authApi.campusAdminLogin(request)
            "coach" -> authApi.coachLogin(request)
            "student" -> authApi.studentLogin(request)
            else -> throw IllegalArgumentException("Unsupported role ${request.role}")
        }
        if (response.code == 20000) {
            response.data?.token?.let { token ->
                tokenManager.saveToken(token, request.role)
            }
        }
        response
    }

    suspend fun logout(): ApiResponse<JsonObject> = withContext(ioDispatcher) {
        val token = tokenManager.tokenFlow.first().orEmpty()
        val body = buildJsonObject {
            put("token", token)
        }
        val result = authApi.logout(body)
        tokenManager.clearToken()
        result
    }

    suspend fun fetchTokenInfo(token: String): ApiResponse<TokenInfo> = withContext(ioDispatcher) {
        authApi.tokenInfo(token)
    }

    suspend fun register(role: String, payload: JsonObject) = withContext(ioDispatcher) {
        authApi.register(role, payload)
    }

    suspend fun updateProfile(role: String, payload: JsonObject) = withContext(ioDispatcher) {
        authApi.updateProfile(role, payload)
    }
}
