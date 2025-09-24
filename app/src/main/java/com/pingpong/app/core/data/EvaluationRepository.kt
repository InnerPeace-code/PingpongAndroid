package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.evaluation.EvaluationItem
import com.pingpong.app.core.network.api.EvaluationApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Singleton
class EvaluationRepository @Inject constructor(
    private val evaluationApi: EvaluationApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getEvaluationsForUser(userId: Long, type: String): Result<List<EvaluationItem>> = withContext(ioDispatcher) {
        runCatching {
            val response = evaluationApi.getByUser(userId, type)
            ensureSuccess(response)
            response.data.parseEvaluations()
        }
    }

    suspend fun getEvaluationsForAppointment(appointmentId: Long): Result<List<EvaluationItem>> = withContext(ioDispatcher) {
        runCatching {
            val response = evaluationApi.getByAppointment(appointmentId)
            ensureSuccess(response)
            response.data.parseEvaluations()
        }
    }

    suspend fun createEvaluation(
        appointmentId: Long,
        evaluatorId: Long,
        evaluatorType: String,
        content: String
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = evaluationApi.createEvaluation(appointmentId, evaluatorId, evaluatorType, content)
            ensureSuccess(response)
        }
    }

    suspend fun updateEvaluation(
        evaluationId: Long,
        content: String,
        evaluatorId: Long
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = evaluationApi.updateEvaluation(evaluationId, content, evaluatorId)
            ensureSuccess(response)
        }
    }

    suspend fun deleteEvaluation(evaluationId: Long, evaluatorId: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = evaluationApi.deleteEvaluation(evaluationId, evaluatorId)
            ensureSuccess(response)
        }
    }

    private fun ensureSuccess(response: com.pingpong.app.core.model.ApiResponse<*>?) {
        if (response == null || response.code != 20000) {
            throw IllegalStateException(response?.message ?: "Request failed")
        }
    }

    private fun JsonElement?.parseEvaluations(): List<EvaluationItem> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.let { obj ->
                obj.asJsonArrayOrNull("data") ?: obj.asJsonArrayOrNull("records")
            }
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: return@mapNotNull null
            EvaluationItem(
                id = id,
                appointmentId = obj.longOrNull("appointmentId"),
                evaluatorId = obj.longOrNull("evaluatorId"),
                evaluatorType = obj.stringOrNull("evaluatorType"),
                content = obj.stringOrNull("content"),
                createdAt = obj.stringOrNull("createTime") ?: obj.stringOrNull("createdAt"),
                updatedAt = obj.stringOrNull("updateTime") ?: obj.stringOrNull("updatedAt")
            )
        }
    }

    private fun JsonObject.asJsonArrayOrNull(key: String): List<JsonElement>? = this[key]?.asJsonArrayOrNull()
}
