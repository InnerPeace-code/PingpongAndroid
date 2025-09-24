package com.pingpong.app.core.network.api

import com.pingpong.app.core.model.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AppointmentApi {

    @GET("/appointment/coach_schedule")
    suspend fun getCoachSchedule(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @POST("/appointment/book")
    suspend fun bookCourse(
        @Query("coachId") coachId: Long,
        @Query("studentId") studentId: Long,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String,
        @Query("tableId") tableId: Long?,
        @Query("autoAssign") autoAssign: Boolean
    ): ApiResponse<JsonElement>

    @POST("/appointment/coach_handle")
    suspend fun handleCoachConfirmation(
        @Query("appointmentId") appointmentId: Long,
        @Query("accept") accept: Boolean
    ): ApiResponse<JsonElement>

    @POST("/appointment/cancel_request")
    suspend fun requestCancel(
        @Query("appointmentId") appointmentId: Long,
        @Query("userId") userId: Long,
        @Query("userType") userType: String
    ): ApiResponse<JsonElement>

    @POST("/appointment/handle_cancel")
    suspend fun handleCancelRequest(
        @Query("cancelRecordId") cancelRecordId: Long,
        @Query("approve") approve: Boolean
    ): ApiResponse<JsonElement>

    @GET("/appointment/student_list")
    suspend fun getStudentAppointments(
        @Query("studentId") studentId: Long
    ): ApiResponse<JsonElement>

    @GET("/appointment/coach_list")
    suspend fun getCoachAppointments(
        @Query("coachId") coachId: Long
    ): ApiResponse<JsonElement>

    @GET("/appointment/pending_cancel_records")
    suspend fun getPendingCancelRecords(
        @Query("userId") userId: Long,
        @Query("userType") userType: String
    ): ApiResponse<JsonElement>

    @GET("/appointment/remaining_cancel_count")
    suspend fun getRemainingCancelCount(
        @Query("userId") userId: Long,
        @Query("userType") userType: String
    ): ApiResponse<JsonElement>
}
