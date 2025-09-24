package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import com.pingpong.app.core.model.LoginRequest
import com.pingpong.app.core.model.TokenInfo
import kotlinx.serialization.json.JsonElement
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {

    @POST("/admin/login")
    suspend fun campusAdminLogin(@Body body: LoginRequest): ApiResponse<JsonElement>

    @POST("/coach/login")
    suspend fun coachLogin(@Body body: LoginRequest): ApiResponse<JsonElement>

    @POST("/student/login")
    suspend fun studentLogin(@Body body: LoginRequest): ApiResponse<JsonElement>

    @GET("/token/info")
    suspend fun tokenInfo(@Query("token") token: String): ApiResponse<TokenInfo>

    @POST("/token/logout")
    suspend fun logout(@Body body: JsonElement): ApiResponse<JsonElement>

    @POST("/{role}/create_user")
    suspend fun register(
        @Path("role") role: String,
        @Body payload: JsonElement
    ): ApiResponse<JsonElement>

    @Multipart
    @POST("/upload/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): ApiResponse<JsonElement>

    @Multipart
    @POST("/upload/coach-photo")
    suspend fun uploadCoachPhoto(@Part file: MultipartBody.Part): ApiResponse<JsonElement>

    @POST("/{role}/update_info")
    suspend fun updateProfile(
        @Path("role") role: String,
        @Body payload: JsonElement
    ): ApiResponse<JsonElement>
}
