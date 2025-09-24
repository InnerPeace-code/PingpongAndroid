package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CampusApi {

    @GET("/common/school_options")
    suspend fun getSchoolOptions(): ApiResponse<JsonObject>

    @GET("/campus/admins")
    suspend fun getCampusAdmins(): ApiResponse<JsonObject>

    @POST("/campus/create")
    suspend fun createCampus(
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @POST("/campus/update/{id}")
    suspend fun updateCampus(
        @Path("id") campusId: Long,
        @Body body: JsonObject
    ): ApiResponse<JsonObject>

    @POST("/campus/delete/{id}")
    suspend fun deleteCampus(
        @Path("id") campusId: Long
    ): ApiResponse<JsonObject>
}
