package com.pingpong.app.core.model.admin

data class AdminCoachSummary(
    val id: Long,
    val name: String?,
    val status: String?,
    val level: Int?,
    val schoolName: String?,
    val phone: String?,
    val email: String?,
    val active: Boolean? = null
)

data class AdminStudentSummary(
    val id: Long,
    val name: String?,
    val phone: String?,
    val email: String?,
    val schoolName: String?,
    val status: String?,
    val balance: Double? = null
)

data class AdminUserSummary(
    val id: Long,
    val username: String?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val schoolId: Long?,
    val schoolName: String?,
    val enabled: Boolean? = null
)

data class SchoolSummary(
    val id: Long,
    val name: String?,
    val address: String? = null,
    val description: String? = null
)
