package com.pingpong.app.core.data

import com.pingpong.app.core.auth.TokenProvider
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
import kotlinx.serialization.json.buildJsonObject

@Singleton
class AdminRepository @Inject constructor(
    private val adminApi: AdminApi,
    private val tokenProvider: TokenProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getCoachRegister(): Result<List<CoachApplication>> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.getCoachRegister(requireToken())
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
                put("coachId", JsonPrimitive(coachId))
                put("isAccepted", JsonPrimitive(isAccepted))
                level?.let { put("level", JsonPrimitive(it)) }
            }
            val response = adminApi.certifyCoach(payload)
            ensureSuccess(response)
        }
    }

    suspend fun getCertifiedCoaches(params: Map<String, String> = emptyMap()): Result<List<AdminCoachSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.getCertifiedCoaches(requireToken(), params)
            ensureSuccess(response)
            response.data.parseAdminCoaches()
        }
    }

    suspend fun getStudents(params: Map<String, String> = emptyMap()): Result<List<AdminStudentSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.getStudents(requireToken(), params)
            ensureSuccess(response)
            response.data.parseAdminStudents()
        }
    }

    suspend fun updateStudent(token: String?, student: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.updateStudent(token ?: requireToken(), student)
            ensureSuccess(response)
        }
    }

    suspend fun updateCertifiedCoach(token: String?, coach: JsonObject): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = adminApi.updateCertifiedCoach(token ?: requireToken(), coach)
            ensureSuccess(response)
        }
    }

    private fun ensureSuccess(response: com.pingpong.app.core.model.ApiResponse<*>?) {
        if (response == null || response.code != 20000) {
            throw IllegalStateException(response?.message ?: "Request failed")
        }
    }

    private fun requireToken(): String = tokenProvider.currentToken()
        ?: throw IllegalStateException("Authentication token missing")

    private fun JsonElement?.parseCoachApplications(): List<CoachApplication> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("list")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val relation = obj.jsonObjectOrNull("relation") ?: obj
            val coach = obj.jsonObjectOrNull("coach") ?: obj
            val student = obj.jsonObjectOrNull("student") ?: obj.jsonObjectOrNull("studentInfo")
            val relationId = relation.longOrNull("id")
                ?: relation.longOrNull("relationId")
                ?: coach.longOrNull("id")
                ?: obj.longOrNull("relationId")
                ?: return@mapNotNull null
            CoachApplication(
                relationId = relationId,
                coachId = coach.longOrNull("id") ?: relation.longOrNull("coachId") ?: obj.longOrNull("coachId"),
                coachName = coach.stringOrNull("realName") ?: coach.stringOrNull("name"),
                coachMale = coach.booleanOrNull("male") ?: coach.booleanOrNull("isMale"),
                coachAge = coach.intOrNull("age"),
                studentId = student?.longOrNull("id") ?: relation.longOrNull("studentId"),
                studentName = student?.stringOrNull("name") ?: student?.stringOrNull("realName") ?: relation.stringOrNull("studentName"),
                studentMale = student?.booleanOrNull("male") ?: student?.booleanOrNull("isMale"),
                studentAge = student?.intOrNull("age"),
                status = relation.stringOrNull("status") ?: obj.stringOrNull("status"),
                appliedAt = relation.stringOrNull("createTime") ?: relation.stringOrNull("createdAt") ?: obj.stringOrNull("createTime")
            )
        }
    }

    private fun JsonElement?.parseAdminCoaches(): List<AdminCoachSummary> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("records")
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: this.asJsonObjectOrNull()?.jsonObjectOrNull("data")?.jsonArrayOrNull("content")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: obj.longOrNull("coachId") ?: return@mapNotNull null
            val certified = obj.booleanOrNull("isCertified") ?: obj.booleanOrNull("certified")
            val statusText = obj.stringOrNull("status") ?: when (certified) {
                true -> "CERTIFIED"
                false -> "PENDING"
                else -> null
            }
            AdminCoachSummary(
                id = id,
                name = obj.stringOrNull("realName") ?: obj.stringOrNull("name"),
                status = statusText,
                level = obj.intOrNull("level"),
                schoolName = obj.stringOrNull("schoolName") ?: obj.stringOrNull("campusName"),
                phone = obj.stringOrNull("phone") ?: obj.stringOrNull("mobile"),
                email = obj.stringOrNull("email"),
                active = certified ?: statusText?.equals("ACTIVE", ignoreCase = true)
            )
        }
    }

    private fun JsonElement?.parseAdminStudents(): List<AdminStudentSummary> {
        if (this == null) return emptyList()

        // 兼容多种列表位置：顶层数组、content、data.content、records、data
        val array: List<JsonElement> =
            this.asJsonArrayOrNull()
                ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("content")
                ?: this.asJsonObjectOrNull()?.jsonObjectOrNull("data")?.jsonArrayOrNull("content")
                ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("records")
                ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
                ?: emptyList()

        return array.mapNotNull { el ->
            val obj = el.asJsonObjectOrNull() ?: return@mapNotNull null

            val id = obj.longOrNull("id") ?: obj.longOrNull("studentId") ?: return@mapNotNull null

            val email = obj.stringOrNull("email")?.takeIf { it.isNotBlank() } // 过滤空字符串
            val schoolName = obj.stringOrNull("schoolName")
                ?: obj.stringOrNull("campusName")
                ?: obj.longOrNull("schoolId")?.toString() // 没有名称时用ID占位

            AdminStudentSummary(
                id = id,
                name = obj.stringOrNull("name")
                    ?: obj.stringOrNull("realName")
                    ?: obj.stringOrNull("username"),
                phone = obj.stringOrNull("phone") ?: obj.stringOrNull("mobile"),
                email = email,
                schoolName = schoolName,
                balance = obj.doubleOrNull("balance")
                    ?: obj.stringOrNull("balance")?.toDoubleOrNull()
                    ?: obj.jsonObjectOrNull("account")?.doubleOrNull("balance")
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
}
