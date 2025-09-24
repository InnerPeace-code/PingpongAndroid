package com.pingpong.app.core.model.coach

data class CoachApplication(
    val relationId: Long,
    val coachId: Long? = null,
    val coachName: String? = null,
    val coachMale: Boolean? = null,
    val coachAge: Int? = null,
    val studentId: Long? = null,
    val studentName: String? = null,
    val studentMale: Boolean? = null,
    val studentAge: Int? = null,
    val status: String? = null,
    val appliedAt: String? = null
)

data class CoachStudent(
    val id: Long,
    val name: String?,
    val male: Boolean?,
    val age: Int?,
    val phone: String?,
    val email: String?,
    val schoolId: Long?,
    val schoolName: String?
)

data class CoachStudentDetail(
    val id: Long,
    val username: String?,
    val name: String?,
    val male: Boolean?,
    val age: Int?,
    val phone: String?,
    val email: String?,
    val avatar: String?,
    val schoolId: Long?,
    val schoolName: String?
)

data class CoachTransaction(
    val id: Long,
    val amount: Double,
    val type: String?,
    val description: String?,
    val createdAt: String?,
    val status: String?
)

data class CoachTransactionPage(
    val records: List<CoachTransaction>,
    val total: Int,
    val page: Int,
    val size: Int
)

data class CoachAccountSnapshot(
    val balance: Double,
    val transactions: CoachTransactionPage
)
