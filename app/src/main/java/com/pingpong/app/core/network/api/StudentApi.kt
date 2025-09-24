package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface StudentApi {

    @GET("/student/get_coach_list")
    suspend fun getCoachList(
        @QueryMap filters: Map<String, String>
    ): ApiResponse<JsonElement>

    @GET("/student/get_coach_detail")
    suspend fun getCoachDetail(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @POST("/student/select_coach")
    suspend fun selectCoach(
        @Query("coachId") coachId: Long,
        @Query("studentId") studentId: Long
    ): ApiResponse<JsonElement>

    @GET("/student/get_related_coaches")
    suspend fun getRelatedCoaches(
        @Query("studentId") studentId: Long
    ): ApiResponse<JsonElement>
}
