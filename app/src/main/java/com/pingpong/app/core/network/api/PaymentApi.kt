package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PaymentApi {

    @GET("/payment/balance")
    suspend fun getBalance(
        @Query("studentId") studentId: Long
    ): ApiResponse<JsonElement>

    @POST("/payment/create")
    suspend fun createPayment(
        @Query("studentId") studentId: Long,
        @Query("amount") amount: Double,
        @Query("method") method: String
    ): ApiResponse<JsonObject>

    @POST("/payment/confirm")
    suspend fun confirmPayment(
        @Query("recordId") recordId: Long
    ): ApiResponse<JsonObject>

    @POST("/payment/cancel")
    suspend fun cancelPayment(
        @Query("recordId") recordId: Long
    ): ApiResponse<JsonObject>

    @GET("/payment/records")
    suspend fun getPaymentRecords(
        @Query("studentId") studentId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: String? = null,
        @Query("method") method: String? = null
    ): ApiResponse<JsonObject>
}
