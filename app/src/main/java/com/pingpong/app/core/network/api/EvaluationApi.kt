package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface EvaluationApi {

    @POST("/evaluation/submit")
    suspend fun createEvaluation(
        @Query("appointmentId") appointmentId: Long,
        @Query("evaluatorId") evaluatorId: Long,
        @Query("evaluatorType") evaluatorType: String,
        @Query("content") content: String
    ): ApiResponse<JsonElement>

    @GET("/evaluation/by_appointment")
    suspend fun getByAppointment(@Query("appointmentId") appointmentId: Long): ApiResponse<JsonElement>

    @GET("/evaluation/by_user")
    suspend fun getByUser(
        @Query("userId") userId: Long,
        @Query("type") type: String
    ): ApiResponse<JsonElement>

    @PUT("/evaluation/update")
    suspend fun updateEvaluation(
        @Query("evaluationId") evaluationId: Long,
        @Query("content") content: String,
        @Query("evaluatorId") evaluatorId: Long
    ): ApiResponse<JsonElement>

    @DELETE("/evaluation/delete")
    suspend fun deleteEvaluation(
        @Query("evaluationId") evaluationId: Long,
        @Query("evaluatorId") evaluatorId: Long
    ): ApiResponse<JsonElement>
}
