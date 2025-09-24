package com.pingpong.app.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchoolOption(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String
)
