package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import com.pingpong.app.core.model.LoginRequest
import com.pingpong.app.core.model.LoginResponse
import com.pingpong.app.core.model.TokenInfo
import kotlinx.serialization.json.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {

    @POST("/super_admin/login")
    suspend fun superAdminLogin(@Body body: LoginRequest): ApiResponse<LoginResponse>

    @POST("/admin/login")
    suspend fun campusAdminLogin(@Body body: LoginRequest): ApiResponse<LoginResponse>

    @POST("/coach/login")
    suspend fun coachLogin(@Body body: LoginRequest): ApiResponse<LoginResponse>

    @POST("/student/login")
    suspend fun studentLogin(@Body body: LoginRequest): ApiResponse<LoginResponse>

    @GET("/token/info")
    suspend fun tokenInfo(@Query("token") token: String): ApiResponse<TokenInfo>

    @POST("/token/logout")
    suspend fun logout(@Body body: JsonObject): ApiResponse<Unit>

    @POST("/{role}/create_user")
    suspend fun register(
        @Path("role") role: String,
        @Body payload: JsonObject
    ): ApiResponse<JsonObject>

    @Multipart
    @POST("/upload/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): ApiResponse<JsonObject>

    @Multipart
    @POST("/upload/coach-photo")
    suspend fun uploadCoachPhoto(@Part file: MultipartBody.Part): ApiResponse<JsonObject>

    @POST("/{role}/update_info")
    suspend fun updateProfile(
        @Path("role") role: String,
        @Body payload: JsonObject
    ): ApiResponse<JsonObject>
}
