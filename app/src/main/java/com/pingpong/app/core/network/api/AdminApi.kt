package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AdminApi {

    @GET("/admin/get_coach_register")
    suspend fun getCoachRegister(
        @Query("token") token: String
    ): ApiResponse<JsonElement>

    @GET("/admin/get_coach_detail")
    suspend fun getCoachDetail(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @POST("/admin/certify_coach")
    suspend fun certifyCoach(
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @GET("/admin/certified-coaches")
    suspend fun getCertifiedCoaches(
        @Query("token") token: String,
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonElement>

    @GET("/admin/get_coaches_by_school")
    suspend fun getCoachesBySchool(
        @Query("token") token: String,
        @Query("schoolId") schoolId: Long
    ): ApiResponse<JsonElement>

    @GET("/admin/get_students_by_school")
    suspend fun getStudentsBySchool(
        @Query("token") token: String,
        @Query("schoolId") schoolId: Long
    ): ApiResponse<JsonElement>

    @GET("/admin/students")
    suspend fun getStudents(
        @Query("token") token: String,
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonElement>

    @POST("/admin/update-student")
    suspend fun updateStudent(
        @Query("token") token: String,
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @POST("/admin/update-certified-coach")
    suspend fun updateCertifiedCoach(
        @Query("token") token: String,
        @Body body: JsonElement
    ): ApiResponse<JsonElement>
}
