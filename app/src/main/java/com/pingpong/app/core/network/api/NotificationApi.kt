package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {

    @GET("/notifications/unread")
    suspend fun getUnreadNotifications(
        @Query("userId") userId: Long,
        @Query("userType") userType: String
    ): ApiResponse<JsonElement>

    @POST("/notifications/mark-read")
    suspend fun markAsRead(
        @Query("notificationId") notificationId: Long
    ): ApiResponse<JsonElement>
}
