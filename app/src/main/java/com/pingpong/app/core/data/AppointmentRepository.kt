package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.booleanOrNull
import com.pingpong.app.core.common.intOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.student.CoachScheduleItem
import com.pingpong.app.core.model.student.PendingCancelRequest
import com.pingpong.app.core.model.student.StudentAppointmentItem
import com.pingpong.app.core.network.api.AppointmentApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

@Singleton
class AppointmentRepository @Inject constructor(
    private val appointmentApi: AppointmentApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getCoachSchedule(coachId: Long): Result<List<CoachScheduleItem>> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.getCoachSchedule(coachId)
            ensureSuccess(response)
            response.data.parseScheduleItems()
        }
    }

    suspend fun bookCourse(
        coachId: Long,
        studentId: Long,
        startTime: String,
        endTime: String,
        tableId: Long?,
        autoAssign: Boolean
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.bookCourse(coachId, studentId, startTime, endTime, tableId, autoAssign)
            ensureSuccess(response)
        }
    }

    suspend fun handleCoachConfirmation(
        appointmentId: Long,
        accept: Boolean
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.handleCoachConfirmation(appointmentId, accept)
            ensureSuccess(response)
        }
    }

    suspend fun requestCancel(
        appointmentId: Long,
        userId: Long,
        userType: String
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.requestCancel(appointmentId, userId, userType)
            ensureSuccess(response)
        }
    }

    suspend fun handleCancelRequest(
        cancelRecordId: Long,
        approve: Boolean
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.handleCancelRequest(cancelRecordId, approve)
            ensureSuccess(response)
        }
    }

    suspend fun getStudentAppointments(studentId: Long): Result<List<StudentAppointmentItem>> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.getStudentAppointments(studentId)
            ensureSuccess(response)
            response.data.parseStudentAppointments()
        }
    }

    suspend fun getCoachAppointments(coachId: Long): Result<List<StudentAppointmentItem>> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.getCoachAppointments(coachId)
            ensureSuccess(response)
            response.data.parseStudentAppointments()
        }
    }

    suspend fun getPendingCancelRecords(
        userId: Long,
        userType: String
    ): Result<List<PendingCancelRequest>> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.getPendingCancelRecords(userId, userType)
            ensureSuccess(response)
            response.data.parsePendingCancelRequests()
        }
    }

    suspend fun getRemainingCancelCount(
        userId: Long,
        userType: String
    ): Result<Int> = withContext(ioDispatcher) {
        runCatching {
            val response = appointmentApi.getRemainingCancelCount(userId, userType)
            ensureSuccess(response)
            response.data?.toRemainingCount() ?: 0
        }
    }

    private fun ensureSuccess(response: com.pingpong.app.core.model.ApiResponse<*>?) {
        if (response == null || response.code != 20000) {
            throw IllegalStateException(response?.message ?: "Request failed")
        }
    }

    private fun JsonElement?.parseScheduleItems(): List<CoachScheduleItem> {
        val array = when {
            this.asJsonArrayOrNull() != null -> this.asJsonArrayOrNull()
            this.asJsonObjectOrNull()?.jsonArrayOrNull("data") != null -> this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            this.asJsonObjectOrNull()?.jsonArrayOrNull("list") != null -> this.asJsonObjectOrNull()?.jsonArrayOrNull("list")
            else -> null
        }
        return array?.mapNotNull { it.asJsonObjectOrNull()?.toScheduleItem() } ?: emptyList()
    }

    private fun JsonObject.toScheduleItem(): CoachScheduleItem = CoachScheduleItem(
        id = longOrNull("id"),
        coachId = longOrNull("coachId"),
        studentId = longOrNull("studentId"),
        startTime = stringOrNull("startTime") ?: stringOrNull("start_time"),
        endTime = stringOrNull("endTime") ?: stringOrNull("end_time"),
        status = stringOrNull("status"),
        tableId = longOrNull("tableId"),
        remarks = stringOrNull("remark") ?: stringOrNull("remarks")
    )

    private fun JsonElement?.parseStudentAppointments(): List<StudentAppointmentItem> {
        val array = when {
            this.asJsonArrayOrNull() != null -> this.asJsonArrayOrNull()
            this.asJsonObjectOrNull()?.jsonArrayOrNull("data") != null -> this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            this.asJsonObjectOrNull()?.jsonArrayOrNull("content") != null -> this.asJsonObjectOrNull()?.jsonArrayOrNull("content")
            else -> null
        }
        return array?.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            StudentAppointmentItem(
                id = obj.longOrNull("id"),
                coachId = obj.longOrNull("coachId"),
                coachName = obj.stringOrNull("coachName") ?: obj.stringOrNull("coach") ?: obj.stringOrNull("name"),
                studentId = obj.longOrNull("studentId"),
                studentName = obj.stringOrNull("studentName")
                    ?: obj.stringOrNull("studentRealName")
                    ?: obj.stringOrNull("student"),
                startTime = obj.stringOrNull("startTime") ?: obj.stringOrNull("start_time"),
                endTime = obj.stringOrNull("endTime") ?: obj.stringOrNull("end_time"),
                status = obj.stringOrNull("status"),
                tableId = obj.longOrNull("tableId"),
                createdAt = obj.stringOrNull("createTime") ?: obj.stringOrNull("createdAt")
            )
        } ?: emptyList()
    }

    private fun JsonElement?.parsePendingCancelRequests(): List<PendingCancelRequest> {
        val array = when {
            this.asJsonArrayOrNull() != null -> this.asJsonArrayOrNull()
            this.asJsonObjectOrNull()?.jsonArrayOrNull("data") != null -> this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            else -> null
        }
        return array?.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: return@mapNotNull null
            PendingCancelRequest(
                id = id,
                appointmentId = obj.longOrNull("appointmentId"),
                coachId = obj.longOrNull("coachId"),
                coachName = obj.stringOrNull("coachName"),
                createTime = obj.stringOrNull("createTime") ?: obj.stringOrNull("createdAt"),
                reason = obj.stringOrNull("reason")
            )
        } ?: emptyList()
    }

    private fun JsonElement.toRemainingCount(): Int {
        return when {
            this is JsonObject -> this["count"]?.jsonPrimitive?.intOrNull ?: 0
            this is JsonArray -> this.size
            this is JsonPrimitive -> this.intOrNull ?: 0
            else -> 0
        }
    }
}
