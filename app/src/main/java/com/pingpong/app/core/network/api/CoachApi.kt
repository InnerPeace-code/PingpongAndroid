package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CoachApi {

    @GET("/coach/selected_by_student")
    suspend fun getStudentApplications(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @GET("/coach/get_student_detail")
    suspend fun getStudentDetail(
        @Query("studentId") studentId: Long
    ): ApiResponse<JsonElement>

    @POST("/coach/review_student_select")
    suspend fun reviewStudentSelect(
        @Query("coachTeachStudentId") applicationId: Long,
        @Query("isAccepted") isAccepted: Boolean
    ): ApiResponse<JsonElement>

    @GET("/coach/get_related_students")
    suspend fun getRelatedStudents(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @GET("/coach/account/balance")
    suspend fun getCoachBalance(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @GET("/coach/account/transactions")
    suspend fun getCoachTransactions(
        @Query("coachId") coachId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("type") type: String? = null
    ): ApiResponse<JsonElement>

    @POST("/coach/account/withdraw")
    suspend fun withdraw(
        @Query("coachId") coachId: Long,
        @Query("amount") amount: Double,
        @Query("bankAccount") bankAccount: String,
        @Query("bankName") bankName: String,
        @Query("accountHolder") accountHolder: String
    ): ApiResponse<JsonElement>
}
