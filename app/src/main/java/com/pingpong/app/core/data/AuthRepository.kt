package com.pingpong.app.core.data

import com.pingpong.app.core.auth.TokenManager
import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asStringOrNull
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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun login(request: LoginRequest): ApiResponse<LoginResponse> = withContext(ioDispatcher) {
        val backendRequest = adjustLoginRequestForBackend(request)
        val apiResponse = when (request.role) {
            "campus_admin" -> authApi.campusAdminLogin(backendRequest)
            "coach" -> authApi.coachLogin(backendRequest)
            "student" -> authApi.studentLogin(backendRequest)
            else -> throw IllegalArgumentException("Unsupported role ${request.role}")
        }
        val token = apiResponse.data.extractToken()
        if (apiResponse.code == 20000 && token != null) {
            tokenManager.saveToken(token, request.role)
        }
        ApiResponse(
            code = apiResponse.code,
            message = apiResponse.message,
            data = token?.let { LoginResponse(token = it) }
        )
    }

    suspend fun logout(): ApiResponse<Unit> = withContext(ioDispatcher) {
        val token = tokenManager.tokenFlow.first().orEmpty()
        val body = buildJsonObject { put("token", JsonPrimitive(token)) }
        val response = authApi.logout(body)
        tokenManager.clearToken()
        ApiResponse(
            code = response.code,
            message = response.message,
            data = null
        )
    }

    suspend fun fetchTokenInfo(token: String): ApiResponse<TokenInfo> = withContext(ioDispatcher) {
        authApi.tokenInfo(token)
    }

    suspend fun register(role: String, payload: JsonObject) = withContext(ioDispatcher) {
        authApi.register(role.backendRole(), payload)
    }

    suspend fun updateProfile(role: String, payload: JsonObject) = withContext(ioDispatcher) {
        authApi.updateProfile(role.backendRole(), payload)
    }

    private fun adjustLoginRequestForBackend(request: LoginRequest): LoginRequest {
        val backendRole = request.role.backendRole()
        return if (backendRole == request.role) request else request.copy(role = backendRole)
    }

    private fun String.backendRole(): String = when (this) {
        "campus_admin" -> "admin"
        else -> this
    }

    private fun JsonElement?.extractToken(): String? {
        return this.asStringOrNull()
            ?: (this as? JsonObject)?.let { obj ->
                obj["token"].asStringOrNull()
                    ?: obj["data"].asStringOrNull()
            }
    }
}
