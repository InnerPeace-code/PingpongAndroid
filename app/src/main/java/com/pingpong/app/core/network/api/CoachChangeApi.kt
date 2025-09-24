package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CoachChangeApi {

    @GET("/coach-change/current-coaches")
    suspend fun getCurrentCoaches(
        @Query("studentId") studentId: Long
    ): ApiResponse<JsonElement>

    @GET("/coach-change/school-coaches")
    suspend fun getSchoolCoaches(
        @Query("studentId") studentId: Long
    ): ApiResponse<JsonElement>

    @POST("/coach-change/submit-request")
    suspend fun submitChangeRequest(
        @Query("studentId") studentId: Long,
        @Query("currentCoachId") currentCoachId: Long,
        @Query("targetCoachId") targetCoachId: Long
    ): ApiResponse<JsonElement>

    @POST("/coach-change/handle-request")
    suspend fun handleChangeRequest(
        @Query("requestId") requestId: Long,
        @Query("handlerId") handlerId: Long,
        @Query("handlerType") handlerType: String,
        @Query("approve") approve: Boolean
    ): ApiResponse<JsonElement>

    @GET("/coach-change/related-requests")
    suspend fun getRelatedRequests(
        @Query("userId") userId: Long,
        @Query("userType") userType: String
    ): ApiResponse<JsonElement>
}
