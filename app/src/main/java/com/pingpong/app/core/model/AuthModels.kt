package com.pingpong.app.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LoginRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("role") val role: String
)

@Serializable
data class LoginResponse(
    @SerialName("token") val token: String
)

@Serializable
data class TokenInfo(
    @SerialName("token") val token: String,
    @SerialName("role") val role: String,
    @SerialName("username") val username: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("realName") val realName: String? = null,
    @SerialName("userId") val userId: Long? = null,
    @SerialName("schoolId") val schoolId: Long? = null,
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("avatarUrl") val avatarUrl: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("userType") val userType: String? = null,
    @SerialName("permissions") val permissions: List<String>? = null
)

@Serializable
data class LogoutRequest(
    @SerialName("token") val token: String
)

@Serializable
data class RegisterRequest(
    @SerialName("role") val role: String,
    @SerialName("payload") val payload: JsonObject
)
