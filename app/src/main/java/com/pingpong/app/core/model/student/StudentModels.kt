package com.pingpong.app.core.model.student

data class StudentCoachFilter(
    val name: String = "",
    val isMale: Boolean? = null,
    val ageLow: Int? = null,
    val ageHigh: Int? = null,
    val level: Int? = null
)

data class CoachSummary(
    val id: Long,
    val name: String,
    val male: Boolean? = null,
    val age: Int? = null,
    val level: Int? = null,
    val schoolId: Long? = null,
    val schoolName: String? = null,
    val description: String? = null,
    val photoPath: String? = null
)

data class CoachDetail(
    val id: Long,
    val name: String,
    val male: Boolean? = null,
    val age: Int? = null,
    val level: Int? = null,
    val schoolId: Long? = null,
    val schoolName: String? = null,
    val description: String? = null,
    val achievements: String? = null,
    val experienceYears: Int? = null,
    val currentStudents: Int? = null,
    val maxStudents: Int? = null,
    val phone: String? = null,
    val email: String? = null,
    val photoPath: String? = null,
    val pricePerHour: Double? = null
)

data class TimeSlot(
    val id: Long? = null,
    val dayOfWeek: Int? = null,
    val startTime: String? = null,
    val endTime: String? = null
)

data class CoachScheduleItem(
    val id: Long? = null,
    val coachId: Long? = null,
    val studentId: Long? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val status: String? = null,
    val tableId: Long? = null,
    val remarks: String? = null
)

data class StudentAppointmentItem(
    val id: Long? = null,
    val coachId: Long? = null,
    val coachName: String? = null,
    val studentId: Long? = null,
    val studentName: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val status: String? = null,
    val tableId: Long? = null,
    val createdAt: String? = null
)

data class PendingCancelRequest(
    val id: Long,
    val appointmentId: Long?,
    val coachId: Long?,
    val coachName: String? = null,
    val createTime: String? = null,
    val reason: String? = null
)

data class PaymentSummary(
    val recordId: Long,
    val qrCodeUrl: String?
)

data class PaymentRecord(
    val id: Long,
    val amount: Double,
    val status: String?,
    val method: String?,
    val type: String? = null,
    val createdAt: String?,
    val updatedAt: String? = null,
    val description: String? = null
)

data class NotificationItem(
    val id: Long,
    val title: String?,
    val content: String?,
    val createdAt: String?,
    val read: Boolean = false
)

data class CoachChangeRequest(
    val id: Long,
    val studentId: Long?,
    val currentCoachId: Long?,
    val targetCoachId: Long?,
    val status: String?,
    val reason: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val studentName: String? = null,
    val currentCoachName: String? = null,
    val targetCoachName: String? = null,
    val currentCoachApproval: Boolean? = null,
    val targetCoachApproval: Boolean? = null
)

data class CoachChangeOption(
    val id: Long,
    val name: String,
    val description: String? = null
)


data class PaymentHistory(
    val records: List<PaymentRecord> = emptyList(),
    val total: Int = 0
)

