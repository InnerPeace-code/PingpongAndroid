package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {

    @GET("/super_admin/schedule/default")
    suspend fun getSuperDefault(): ApiResponse<JsonArray>

    @GET("/admin/schedule/default")
    suspend fun getAdminDefault(): ApiResponse<JsonArray>

    @GET("/super_admin/schedule/schools")
    suspend fun getSuperSchools(): ApiResponse<JsonObject>

    @GET("/admin/schedule/managed-schools")
    suspend fun getAdminSchools(@Query("token") token: String): ApiResponse<JsonObject>

    @GET("/super_admin/schedule/check/{schoolId}")
    suspend fun superCheck(@Path("schoolId") schoolId: Long): ApiResponse<JsonObject>

    @GET("/admin/schedule/check/{schoolId}")
    suspend fun adminCheck(@Path("schoolId") schoolId: Long): ApiResponse<JsonObject>

    @POST("/super_admin/schedule/save/{schoolId}")
    suspend fun superSave(
        @Path("schoolId") schoolId: Long,
        @Body schedules: JsonArray
    ): ApiResponse<JsonObject>

    @POST("/admin/schedule/save/{schoolId}")
    suspend fun adminSave(
        @Path("schoolId") schoolId: Long,
        @Body schedules: JsonArray
    ): ApiResponse<JsonObject>

    @GET("/super_admin/schedule/school/{schoolId}")
    suspend fun getSuperSchedule(@Path("schoolId") schoolId: Long): ApiResponse<JsonArray>

    @GET("/admin/schedule/school/{schoolId}")
    suspend fun getAdminSchedule(@Path("schoolId") schoolId: Long): ApiResponse<JsonArray>

    @GET("/super_admin/schedule/existing-templates")
    suspend fun getSuperTemplates(): ApiResponse<JsonObject>

    @GET("/admin/schedule/existing-templates")
    suspend fun getAdminTemplates(@Query("token") token: String): ApiResponse<JsonObject>

    @GET("/super_admin/schedule/template/{schoolId}")
    suspend fun getSuperTemplate(@Path("schoolId") schoolId: Long): ApiResponse<JsonArray>

    @GET("/admin/schedule/template/{schoolId}")
    suspend fun getAdminTemplate(@Path("schoolId") schoolId: Long): ApiResponse<JsonArray>
}
