package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CampusApi {

    @GET("/common/school_options")
    suspend fun getSchoolOptions(): ApiResponse<JsonElement>

    @GET("/campus/admins")
    suspend fun getCampusAdmins(): ApiResponse<JsonElement>

    @POST("/campus/create")
    suspend fun createCampus(
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @POST("/campus/update/{id}")
    suspend fun updateCampus(
        @Path("id") campusId: Long,
        @Body body: JsonElement
    ): ApiResponse<JsonElement>

    @POST("/campus/delete/{id}")
    suspend fun deleteCampus(
        @Path("id") campusId: Long
    ): ApiResponse<JsonElement>
}
