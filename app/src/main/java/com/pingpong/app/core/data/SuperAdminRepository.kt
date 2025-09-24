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
import com.pingpong.app.core.model.admin.AdminUserSummary
import com.pingpong.app.core.model.admin.SchoolSummary
import com.pingpong.app.core.model.student.CoachApplication
import com.pingpong.app.core.model.student.CoachDetail
import com.pingpong.app.core.network.api.SuperAdminApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Singleton
class SuperAdminRepository @Inject constructor(
    private val superAdminApi: SuperAdminApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getAdmins(): Result<List<AdminUserSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = superAdminApi.getAdmins()
            ensureSuccess(response)
            response.data.parseAdminUsers()
        }
    }

    suspend fun createAdmin(body: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.createAdmin(body))
        }
    }

    suspend fun updateAdmin(body: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.editAdmin(body))
        }
    }

    suspend fun deleteAdmin(id: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.deleteAdmin(id))
        }
    }

    suspend fun getSchools(): Result<List<SchoolSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = superAdminApi.getSchools()
            ensureSuccess(response)
            response.data.parseSchools()
        }
    }

    suspend fun createSchool(body: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.createSchool(body))
        }
    }

    suspend fun updateSchool(body: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.updateSchool(body))
        }
    }

    suspend fun deleteSchool(id: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.deleteSchool(id))
        }
    }

    suspend fun getAllUncertifiedCoaches(): Result<List<CoachApplication>> = withContext(ioDispatcher) {
        runCatching {
            val response = superAdminApi.getAllUncertifiedCoaches()
            ensureSuccess(response)
            response.data.parseCoachApplications()
        }
    }

    suspend fun getCoachDetail(coachId: Long): Result<CoachDetail> = withContext(ioDispatcher) {
        runCatching {
            val response = superAdminApi.getCoachDetail(coachId)
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
            ensureSuccess(superAdminApi.certifyCoach(payload))
        }
    }

    suspend fun getAllStudents(params: Map<String, String> = emptyMap()): Result<List<AdminStudentSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = superAdminApi.getAllStudents(params)
            ensureSuccess(response)
            response.data.parseAdminStudents()
        }
    }

    suspend fun getAllCertifiedCoaches(params: Map<String, String> = emptyMap()): Result<List<AdminCoachSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = superAdminApi.getAllCertifiedCoaches(params)
            ensureSuccess(response)
            response.data.parseAdminCoaches()
        }
    }

    suspend fun updateStudent(token: String?, body: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.updateStudent(token, body))
        }
    }

    suspend fun updateCertifiedCoach(token: String?, body: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            ensureSuccess(superAdminApi.updateCertifiedCoach(token, body))
        }
    }

    private fun ensureSuccess(response: com.pingpong.app.core.model.ApiResponse<*>?) {
        if (response == null || response.code != 20000) {
            throw IllegalStateException(response?.message ?: "Request failed")
        }
    }

    private fun JsonElement?.parseAdminUsers(): List<AdminUserSummary> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: obj.longOrNull("adminId") ?: return@mapNotNull null
            AdminUserSummary(
                id = id,
                username = obj.stringOrNull("username"),
                name = obj.stringOrNull("name") ?: obj.stringOrNull("realName"),
                phone = obj.stringOrNull("phone") ?: obj.stringOrNull("mobile"),
                email = obj.stringOrNull("email"),
                schoolId = obj.longOrNull("schoolId"),
                schoolName = obj.stringOrNull("schoolName"),
                enabled = obj.booleanOrNull("enabled") ?: obj.stringOrNull("status")?.equals("ACTIVE", true)
            )
        }
    }

    private fun JsonElement?.parseSchools(): List<SchoolSummary> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: return@mapNotNull null
            SchoolSummary(
                id = id,
                name = obj.stringOrNull("name"),
                address = obj.stringOrNull("address"),
                description = obj.stringOrNull("description")
            )
        }
    }

    private fun JsonElement?.parseCoachApplications(): List<CoachApplication> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val relationId = obj.longOrNull("id") ?: obj.longOrNull("coachId") ?: return@mapNotNull null
            CoachApplication(
                relationId = relationId,
                studentId = null,
                studentName = obj.stringOrNull("realName") ?: obj.stringOrNull("name"),
                studentMale = obj.booleanOrNull("male") ?: obj.booleanOrNull("isMale"),
                studentAge = obj.intOrNull("age"),
                status = obj.stringOrNull("status"),
                appliedAt = obj.stringOrNull("createTime") ?: obj.stringOrNull("createdAt"),
                coachId = obj.longOrNull("id") ?: obj.longOrNull("coachId")
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
