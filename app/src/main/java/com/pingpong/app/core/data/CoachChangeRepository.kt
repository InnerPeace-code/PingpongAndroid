package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.booleanOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.student.CoachChangeOption
import com.pingpong.app.core.model.student.CoachChangeRequest
import com.pingpong.app.core.network.api.CoachChangeApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

@Singleton
class CoachChangeRepository @Inject constructor(
    private val coachChangeApi: CoachChangeApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getCurrentCoaches(studentId: Long): Result<List<CoachChangeOption>> = withContext(ioDispatcher) {
        runCatching {
            val response = coachChangeApi.getCurrentCoaches(studentId)
            ensureSuccess(response)
            response.data.parseCoachOptions()
        }
    }

    suspend fun getSchoolCoaches(studentId: Long): Result<List<CoachChangeOption>> = withContext(ioDispatcher) {
        runCatching {
            val response = coachChangeApi.getSchoolCoaches(studentId)
            ensureSuccess(response)
            response.data.parseCoachOptions()
        }
    }

    suspend fun submitChangeRequest(
        studentId: Long,
        currentCoachId: Long,
        targetCoachId: Long
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = coachChangeApi.submitChangeRequest(studentId, currentCoachId, targetCoachId)
            ensureSuccess(response)
        }
    }

    suspend fun handleChangeRequest(
        requestId: Long,
        handlerId: Long,
        handlerType: String,
        approve: Boolean
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = coachChangeApi.handleChangeRequest(requestId, handlerId, handlerType, approve)
            ensureSuccess(response)
        }
    }

    suspend fun getRelatedRequests(userId: Long, userType: String): Result<List<CoachChangeRequest>> = withContext(ioDispatcher) {
        runCatching {
            val response = coachChangeApi.getRelatedRequests(userId, userType)
            ensureSuccess(response)
            response.data.parseChangeRequests()
        }
    }

    private fun ensureSuccess(response: com.pingpong.app.core.model.ApiResponse<*>?) {
        if (response == null || response.code != 20000) {
            throw IllegalStateException(response?.message ?: "Request failed")
        }
    }

    private fun JsonElement?.parseCoachOptions(): List<CoachChangeOption> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: obj.longOrNull("coachId") ?: return@mapNotNull null
            val name = obj.stringOrNull("name") ?: obj.stringOrNull("realName") ?: return@mapNotNull null
            CoachChangeOption(
                id = id,
                name = name,
                description = obj.stringOrNull("description") ?: obj.stringOrNull("achievements")
            )
        }
    }

    private fun JsonElement?.parseChangeRequests(): List<CoachChangeRequest> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.jsonArrayOrNull("data")
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: return@mapNotNull null
            CoachChangeRequest(
                id = id,
                studentId = obj.longOrNull("studentId"),
                currentCoachId = obj.longOrNull("currentCoachId"),
                targetCoachId = obj.longOrNull("targetCoachId"),
                status = obj.stringOrNull("status"),
                reason = obj.stringOrNull("reason"),
                createdAt = obj.stringOrNull("createTime") ?: obj.stringOrNull("createdAt"),
                updatedAt = obj.stringOrNull("updateTime") ?: obj.stringOrNull("updatedAt"),
                studentName = obj.stringOrNull("studentName"),
                currentCoachName = obj.stringOrNull("currentCoachName"),
                targetCoachName = obj.stringOrNull("targetCoachName"),
                currentCoachApproval = obj.booleanOrNull("currentCoachApproval"),
                targetCoachApproval = obj.booleanOrNull("targetCoachApproval")
            )
        }
    }
}
