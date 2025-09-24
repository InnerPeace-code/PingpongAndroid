package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.booleanOrNull
import com.pingpong.app.core.common.doubleOrNull
import com.pingpong.app.core.common.intOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.jsonObjectOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.admin.AdminCoachSummary
import com.pingpong.app.core.model.admin.AdminStudentSummary
import com.pingpong.app.core.model.coach.CoachApplication
import com.pingpong.app.core.model.student.CoachDetail
import com.pingpong.app.core.network.api.AdminApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Singleton
class AdminRepository @Inject constructor(
    private val adminApi: AdminApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getCoachRegister(): Result<List<CoachApplication>> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.getCoachRegister()
            ensureSuccess(response)
            response.data.parseCoachApplications()
        }
    }

    suspend fun getCoachDetail(coachId: Long): Result<CoachDetail> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.getCoachDetail(coachId)
            ensureSuccess(response)
            val data = response.data?.asJsonObjectOrNull()
                ?: response.data?.jsonObjectOrNull("data")
                ?: throw IllegalStateException("Coach detail unavailable")
            data.toCoachDetail()
        }
    }

    suspend fun certifyCoach(coachId: Long, isAccepted: Boolean, level: Int?): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val payload = buildJsonObject {
                put("coachId", coachId)
                put("isAccepted", isAccepted)
                level?.let { put("level", it) }
            }
            val response = adminApi.certifyCoach(payload)
            ensureSuccess(response)
        }
    }

    suspend fun getCertifiedCoaches(params: Map<String, String> = emptyMap()): Result<List<AdminCoachSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.getCertifiedCoaches(params)
            ensureSuccess(response)
            response.data.parseAdminCoaches()
        }
    }

    suspend fun updateCoachStatus(coachId: Long, status: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val payload = buildJsonObject { put("status", JsonPrimitive(status)) }
            val response = adminApi.updateCoachStatus(coachId, payload)
            ensureSuccess(response)
        }
    }

    suspend fun getStudents(params: Map<String, String> = emptyMap()): Result<List<AdminStudentSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.getStudents(params)
            ensureSuccess(response)
            response.data.parseAdminStudents()
        }
    }

    suspend fun updateStudent(token: String?, student: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.updateStudent(token, student)
            ensureSuccess(response)
        }
    }

    suspend fun updateCertifiedCoach(token: String?, coach: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.updateCertifiedCoach(token, coach)
            ensureSuccess(response)
        }
    }

    private fun ensureSuccess(response: com.pingpong.app.core.model.ApiResponse<*>?) {
        if (response == null || response.code != 20000) {
            throw IllegalStateException(response?.message ?: "Request failed")
        }
    }

    private fun JsonElement?.parseCoachApplications(): List<CoachApplication> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("list")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val relation = obj.jsonObjectOrNull("relation") ?: obj
            val coach = obj.jsonObjectOrNull("coach") ?: obj
            val relationId = relation.longOrNull("id") ?: relation.longOrNull("relationId") ?: coach.longOrNull("id") ?: return@mapNotNull null
            CoachApplication(
                relationId = relationId,
                studentId = relation.longOrNull("studentId"),
                studentName = coach.stringOrNull("realName") ?: coach.stringOrNull("name"),
                studentMale = coach.booleanOrNull("male") ?: coach.booleanOrNull("isMale"),
                studentAge = coach.intOrNull("age"),
                status = relation.stringOrNull("status"),
                appliedAt = relation.stringOrNull("createTime") ?: relation.stringOrNull("createdAt")
            )
        }
    }

    private fun JsonElement?.parseAdminCoaches(): List<AdminCoachSummary> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("records")
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: obj.longOrNull("coachId") ?: return@mapNotNull null
            AdminCoachSummary(
                id = id,
                name = obj.stringOrNull("realName") ?: obj.stringOrNull("name"),
                status = obj.stringOrNull("status"),
                level = obj.intOrNull("level"),
                schoolName = obj.stringOrNull("schoolName") ?: obj.stringOrNull("campusName"),
                phone = obj.stringOrNull("phone") ?: obj.stringOrNull("mobile"),
                email = obj.stringOrNull("email"),
                active = obj.stringOrNull("status")?.equals("ACTIVE", ignoreCase = true)
            )
        }
    }

    private fun JsonElement?.parseAdminStudents(): List<AdminStudentSummary> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("records")
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: obj.longOrNull("studentId") ?: return@mapNotNull null
            AdminStudentSummary(
                id = id,
                name = obj.stringOrNull("name") ?: obj.stringOrNull("realName"),
                phone = obj.stringOrNull("phone") ?: obj.stringOrNull("mobile"),
                email = obj.stringOrNull("email"),
                schoolName = obj.stringOrNull("schoolName") ?: obj.stringOrNull("campusName"),
                status = obj.stringOrNull("status"),
                balance = obj.doubleOrNull("balance") ?: obj.stringOrNull("balance")?.toDoubleOrNull()
            )
        }
    }

    private fun JsonObject.toCoachDetail(): CoachDetail {
        val baseId = longOrNull("id") ?: longOrNull("coachId") ?: throw IllegalStateException("Missing coach id")
        return CoachDetail(
            id = baseId,
            name = stringOrNull("realName") ?: stringOrNull("name") ?: "",
            male = booleanOrNull("male") ?: booleanOrNull("isMale"),
            age = intOrNull("age"),
            level = intOrNull("level"),
            schoolId = longOrNull("schoolId"),
            schoolName = stringOrNull("schoolName") ?: stringOrNull("campusName"),
            description = stringOrNull("description") ?: stringOrNull("intro"),
            achievements = stringOrNull("achievements") ?: stringOrNull("honor"),
            experienceYears = intOrNull("experienceYears") ?: intOrNull("years"),
            currentStudents = intOrNull("currentStudents") ?: intOrNull("currentStudentCount"),
            maxStudents = intOrNull("maxStudents") ?: intOrNull("maxStudentCount"),
            phone = stringOrNull("phone") ?: stringOrNull("mobile"),
            email = stringOrNull("email"),
            photoPath = stringOrNull("photoPath") ?: stringOrNull("avatar"),
            pricePerHour = doubleOrNull("price") ?: stringOrNull("price")?.toDoubleOrNull()
        )
    }

    private fun buildJsonObject(builder: kotlinx.serialization.json.JsonObjectBuilder.() -> Unit): JsonObject {
        return kotlinx.serialization.json.buildJsonObject(builder)
    }

    private fun kotlinx.serialization.json.JsonObjectBuilder.put(key: String, value: Long) {
        this.put(key, kotlinx.serialization.json.JsonPrimitive(value))
    }

    private fun kotlinx.serialization.json.JsonObjectBuilder.put(key: String, value: Boolean) {
        this.put(key, kotlinx.serialization.json.JsonPrimitive(value))
    }

    private fun kotlinx.serialization.json.JsonObjectBuilder.put(key: String, value: Int) {
        this.put(key, kotlinx.serialization.json.JsonPrimitive(value))
    }
}
