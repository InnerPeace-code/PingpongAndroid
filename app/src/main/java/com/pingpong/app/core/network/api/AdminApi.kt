package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AdminApi {

    @GET("/admin/get_coach_register")
    suspend fun getCoachRegister(
        @Query("token") token: String? = null
    ): ApiResponse<JsonObject>

    @GET("/admin/get_coach_detail")
    suspend fun getCoachDetail(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonObject>

    @POST("/admin/certify_coach")
    suspend fun certifyCoach(
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @GET("/admin/certified_coaches")
    suspend fun getCertifiedCoaches(
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonObject>

    @POST("/admin/coach_status/{id}")
    suspend fun updateCoachStatus(
        @Path("id") coachId: Long,
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @GET("/admin/get_coaches_by_school")
    suspend fun getCoachesBySchool(
        @Query("token") token: String? = null,
        @Query("schoolId") schoolId: Long
    ): ApiResponse<JsonObject>

    @GET("/admin/get_students_by_school")
    suspend fun getStudentsBySchool(
        @Query("token") token: String? = null,
        @Query("schoolId") schoolId: Long
    ): ApiResponse<JsonObject>

    @GET("/admin/students")
    suspend fun getStudents(
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonObject>

    @GET("/admin/certified-coaches")
    suspend fun getCertifiedCoachesLite(
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonObject>

    @POST("/admin/update-student")
    suspend fun updateStudent(
        @Query("token") token: String? = null,
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @POST("/admin/update-certified-coach")
    suspend fun updateCertifiedCoach(
        @Query("token") token: String? = null,
        @Body body: JsonObject
    ): ApiResponse<JsonObject>
}
