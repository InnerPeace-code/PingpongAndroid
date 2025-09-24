package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SuperAdminApi {

    @GET("/super_admin/admins")
    suspend fun getAdmins(): ApiResponse<JsonElement>

    @POST("/super_admin/create_admin")
    suspend fun createAdmin(
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @POST("/super_admin/edit_admin")
    suspend fun editAdmin(
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @DELETE("/super_admin/delete_admin/{id}")
    suspend fun deleteAdmin(@Path("id") id: Long): ApiResponse<JsonElement>

    @GET("/super_admin/schools")
    suspend fun getSchools(): ApiResponse<JsonElement>

    @POST("/super_admin/create_school")
    suspend fun createSchool(
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @POST("/super_admin/manage_school_info")
    suspend fun updateSchool(
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @DELETE("/super_admin/delete_school/{id}")
    suspend fun deleteSchool(@Path("id") id: Long): ApiResponse<JsonElement>

    @GET("/super_admin/get_all_uncertified_coaches")
    suspend fun getAllUncertifiedCoaches(): ApiResponse<JsonElement>

    @GET("/super_admin/get_super_coach_detail")
    suspend fun getCoachDetail(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @POST("/super_admin/super_certify_coach")
    suspend fun certifyCoach(
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @GET("/super_admin/all-students")
    suspend fun getAllStudents(
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonElement>

    @GET("/super_admin/all-certified-coaches")
    suspend fun getAllCertifiedCoaches(
        @QueryMap params: Map<String, String>
    ): ApiResponse<JsonElement>

    @POST("/super_admin/update-student")
    suspend fun updateStudent(
        @Query("token") token: String,
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @POST("/super_admin/update-certified-coach")
    suspend fun updateCertifiedCoach(
        @Query("token") token: String,
        @Body body: JsonElement
    ): ApiResponse<JsonElement>
}
