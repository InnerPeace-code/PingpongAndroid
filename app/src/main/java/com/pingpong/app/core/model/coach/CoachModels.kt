package com.pingpong.app.core.model.coach

data class CoachApplication(
    val relationId: Long,
    val studentId: Long?,
    val studentName: String?,
    val studentMale: Boolean?,
    val studentAge: Int?,
    val status: String?,
    val appliedAt: String?
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
