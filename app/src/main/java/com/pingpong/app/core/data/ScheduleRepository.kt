package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.booleanOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.admin.SchoolSummary
import com.pingpong.app.core.model.student.TimeSlot
import com.pingpong.app.core.network.api.ScheduleApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Singleton
class ScheduleRepository @Inject constructor(
    private val scheduleApi: ScheduleApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getAvailableSchools(isSuperAdmin: Boolean, token: String?): Result<List<SchoolSummary>> = withContext(ioDispatcher) {
        runCatching {
            val response = if (isSuperAdmin) {
                scheduleApi.getSuperSchools()
            } else {
                val resolvedToken = token ?: throw IllegalStateException("Missing auth token")
                scheduleApi.getAdminSchools(resolvedToken)
            }
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to load schools")
            }
            response.data.parseSchools()
        }
    }

    suspend fun getSchoolSchedule(schoolId: Long, isSuper: Boolean): Result<List<TimeSlot>> = withContext(ioDispatcher) {
        runCatching {
            val response = if (isSuper) {
                scheduleApi.getSuperSchedule(schoolId)
            } else {
                scheduleApi.getAdminSchedule(schoolId)
            }
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to load schedule")
            }
            response.data?.toTimeSlots() ?: emptyList()
        }
    }

    suspend fun checkSchoolHasSchedule(schoolId: Long, isSuper: Boolean): Result<Boolean> = withContext(ioDispatcher) {
        runCatching {
            val response = if (isSuper) {
                scheduleApi.superCheck(schoolId)
            } else {
                scheduleApi.adminCheck(schoolId)
            }
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to check schedule")
            }
            val data = response.data
            val obj = data?.asJsonObjectOrNull()
            when {
                data == null -> false
                data.asJsonArrayOrNull()?.isNotEmpty() == true -> true
                obj?.jsonArrayOrNull("data")?.isNotEmpty() == true -> true
                obj?.stringOrNull("hasSchedule") != null -> obj.stringOrNull("hasSchedule")!!.toBoolean()
                else -> obj?.booleanOrNull("exists") ?: obj?.booleanOrNull("hasSchedule") ?: false
            }
        }
    }

    private fun JsonArray.toTimeSlots(): List<TimeSlot> = mapNotNull { element ->
        val obj = element as? JsonObject ?: return@mapNotNull null
        TimeSlot(
            id = obj.longOrNull("id"),
            dayOfWeek = obj.longOrNull("dayOfWeek")?.toInt() ?: obj.longOrNull("weekday")?.toInt(),
            startTime = obj.stringOrNull("startTime") ?: obj.stringOrNull("start_time"),
            endTime = obj.stringOrNull("endTime") ?: obj.stringOrNull("end_time")
        )
    }

    private fun JsonElement?.parseSchools(): List<SchoolSummary> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("records")
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
}
