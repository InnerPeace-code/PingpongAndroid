package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.booleanOrNull
import com.pingpong.app.core.common.doubleOrNull
import com.pingpong.app.core.common.intOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.student.CoachDetail
import com.pingpong.app.core.model.student.CoachSummary
import com.pingpong.app.core.model.student.StudentCoachFilter
import com.pingpong.app.core.network.api.StudentApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Singleton
class StudentCoachRepository @Inject constructor(
    private val studentApi: StudentApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getCoachList(
        filter: StudentCoachFilter,
        schoolId: Long?
    ): Result<List<CoachSummary>> = withContext(ioDispatcher) {
        runCatching {
            val params = buildQueryMap(filter, schoolId)
            val response = studentApi.getCoachList(params)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to load coaches")
            }
            response.data.parseCoachArray()
        }
    }

    suspend fun getCoachDetail(coachId: Long): Result<CoachDetail> = withContext(ioDispatcher) {
        runCatching {
            val response = studentApi.getCoachDetail(coachId)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to load coach detail")
            }
            val json = response.data?.asJsonObjectOrNull()
                ?: throw IllegalStateException("Coach detail unavailable")
            json.toCoachDetail()
        }
    }

    suspend fun selectCoach(
        coachId: Long,
        studentId: Long
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = studentApi.selectCoach(coachId = coachId, studentId = studentId)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to submit request")
            }
        }
    }

    suspend fun getRelatedCoaches(studentId: Long): Result<List<CoachSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = studentApi.getRelatedCoaches(studentId)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to load related coaches")
            }
            response.data.parseCoachArray()
        }
    }

    private fun buildQueryMap(filter: StudentCoachFilter, schoolId: Long?): Map<String, String> = buildMap {
        schoolId?.let { put("schoolId", it.toString()) }
        if (filter.name.isNotBlank()) put("name", filter.name)
        filter.isMale?.let { put("isMale", it.toString()) }
        filter.ageLow?.let { put("age_low", it.toString()) }
        filter.ageHigh?.let { put("age_high", it.toString()) }
        filter.level?.let { put("level", it.toString()) }
    }

    private fun JsonElement?.parseCoachArray(): List<CoachSummary> {
        val direct = this.asJsonArrayOrNull()?.mapNotNull { it.asJsonObjectOrNull()?.toCoachSummary() }
        if (direct != null) return direct
        val obj = this.asJsonObjectOrNull() ?: return emptyList()
        val nestedArrays = listOf("data", "list", "records", "content")
        nestedArrays.forEach { key ->
            val array = obj.jsonArrayOrNull(key)?.mapNotNull { it.asJsonObjectOrNull()?.toCoachSummary() }
            if (array != null) return array
        }
        return emptyList()
    }

    private fun JsonObject.toCoachSummary(): CoachSummary? {
        val id = longOrNull("id") ?: longOrNull("coachId") ?: return null
        val name = stringOrNull("name") ?: stringOrNull("realName") ?: return null
        return CoachSummary(
            id = id,
            name = name,
            male = booleanOrNull("male") ?: booleanOrNull("isMale"),
            age = intOrNull("age"),
            level = intOrNull("level"),
            schoolId = longOrNull("schoolId"),
            schoolName = stringOrNull("schoolName") ?: stringOrNull("campusName"),
            description = stringOrNull("description") ?: stringOrNull("intro") ?: stringOrNull("achievements"),
            photoPath = stringOrNull("photoPath") ?: stringOrNull("avatar")
        )
    }

    private fun JsonObject.toCoachDetail(): CoachDetail {
        val base = toCoachSummary()
        val id = base?.id ?: longOrNull("id") ?: longOrNull("coachId") ?: error("Missing coach id")
        val name = base?.name ?: stringOrNull("name") ?: stringOrNull("realName") ?: "" 
        return CoachDetail(
            id = id,
            name = name,
            male = base?.male ?: booleanOrNull("male") ?: booleanOrNull("isMale"),
            age = base?.age ?: intOrNull("age"),
            level = base?.level ?: intOrNull("level"),
            schoolId = base?.schoolId ?: longOrNull("schoolId"),
            schoolName = base?.schoolName ?: stringOrNull("schoolName") ?: stringOrNull("campusName"),
            description = stringOrNull("description") ?: base?.description,
            achievements = stringOrNull("achievements") ?: stringOrNull("honor") ?: stringOrNull("awards"),
            experienceYears = intOrNull("experienceYears") ?: intOrNull("years"),
            currentStudents = intOrNull("currentStudents") ?: intOrNull("currentStudentCount"),
            maxStudents = intOrNull("maxStudents") ?: intOrNull("maxStudentCount"),
            phone = stringOrNull("phone") ?: stringOrNull("mobile"),
            email = stringOrNull("email"),
            photoPath = stringOrNull("photoPath") ?: base?.photoPath,
            pricePerHour = doubleOrNull("price") ?: doubleOrNull("pricePerHour")
        )
    }
}
