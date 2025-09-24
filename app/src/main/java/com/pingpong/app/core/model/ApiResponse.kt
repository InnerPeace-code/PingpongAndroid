package com.pingpong.app.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: T? = null
)
