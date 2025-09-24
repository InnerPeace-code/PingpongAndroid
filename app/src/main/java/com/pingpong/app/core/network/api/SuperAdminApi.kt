package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SuperAdminApi {

    @GET("/super_admin/admins")
    suspend fun getAdmins(): ApiResponse<JsonObject>

    @POST("/super_admin/create_admin")
    suspend fun createAdmin(
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @POST("/super_admin/edit_admin")
    suspend fun editAdmin(
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @DELETE("/super_admin/delete_admin/{id}")
    suspend fun deleteAdmin(@Path("id") id: Long): ApiResponse<JsonObject>

    @GET("/super_admin/schools")
    suspend fun getSchools(): ApiResponse<JsonObject>

    @POST("/super_admin/create_school")
    suspend fun createSchool(
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @POST("/super_admin/manage_school_info")
    suspend fun updateSchool(
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @DELETE("/super_admin/delete_school/{id}")
    suspend fun deleteSchool(@Path("id") id: Long): ApiResponse<JsonObject>

    @GET("/super_admin/get_all_uncertified_coaches")
    suspend fun getAllUncertifiedCoaches(): ApiResponse<JsonObject>

    @GET("/super_admin/get_super_coach_detail")
    suspend fun getCoachDetail(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonObject>

    @POST("/super_admin/super_certify_coach")
    suspend fun certifyCoach(
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @GET("/super_admin/all-students")
    suspend fun getAllStudents(
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonObject>

    @GET("/super_admin/all-certified-coaches")
    suspend fun getAllCertifiedCoaches(
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonObject>

    @POST("/super_admin/update-student")
    suspend fun updateStudent(
        @Query("token") token: String? = null,
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @POST("/super_admin/update-certified-coach")
    suspend fun updateCertifiedCoach(
        @Query("token") token: String? = null,
        @Body body: JsonObject
    ): ApiResponse<JsonObject>
}
