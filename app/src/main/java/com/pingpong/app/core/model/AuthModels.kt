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
    @SerialName("username") val username: String? = null,
    @SerialName("password") val password: String? = null,
    @SerialName("age") val age: Int? = null,
    @SerialName("isMale") private val isMaleFlag: Boolean? = null,
    @SerialName("male") private val maleFlag: Boolean? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("schoolId") val schoolId: Long? = null,
    @SerialName("role") private val backendRole: String? = null,
    @SerialName("UserId") private val backendUserId: Long? = null,
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("avatarUrl") val avatarUrl: String? = null,
    @SerialName("photoPath") val photoPath: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("name") val name: String? = null
) {
    val role: String?
        get() = when (backendRole?.lowercase()) {
            "admin" -> "campus_admin"
            else -> backendRole
        }

    val userId: Long?
        get() = backendUserId

    val male: Boolean?
        get() = isMaleFlag ?: maleFlag

    val userType: String?
        get() = role?.uppercase()
}

@Serializable
data class LogoutRequest(
    @SerialName("token") val token: String
)

@Serializable
data class RegisterRequest(
    @SerialName("role") val role: String,
    @SerialName("payload") val payload: JsonObject
)
