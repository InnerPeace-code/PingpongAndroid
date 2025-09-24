package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asDoubleOrNull
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.booleanOrNull
import com.pingpong.app.core.common.doubleOrNull
import com.pingpong.app.core.common.intOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.student.PaymentHistory
import com.pingpong.app.core.model.student.PaymentRecord
import com.pingpong.app.core.model.student.PaymentSummary
import com.pingpong.app.core.network.api.PaymentApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentApi: PaymentApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getBalance(studentId: Long): Result<Double> = withContext(ioDispatcher) {
        runCatching {
            val response = paymentApi.getBalance(studentId)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to fetch balance")
            }
            response.data?.asDoubleOrNull() ?: response.data?.stringOrNull()?.toDoubleOrNull() ?: 0.0
        }
    }

    suspend fun createPayment(
        studentId: Long,
        amount: Double,
        method: String
    ): Result<PaymentSummary> = withContext(ioDispatcher) {
        runCatching {
            val response = paymentApi.createPayment(studentId, amount, method)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to create payment")
            }
            val data = response.data ?: throw IllegalStateException("Missing payment info")
            PaymentSummary(
                recordId = data.longOrNull("id") ?: throw IllegalStateException("Missing payment record id"),
                qrCodeUrl = data.stringOrNull("qrCodeUrl") ?: data.stringOrNull("qrcode")
            )
        }
    }

    suspend fun confirmPayment(recordId: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = paymentApi.confirmPayment(recordId)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to confirm payment")
            }
        }
    }

    suspend fun cancelPayment(recordId: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = paymentApi.cancelPayment(recordId)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to cancel payment")
            }
        }
    }

    suspend fun getPaymentRecords(
        studentId: Long,
        page: Int,
        size: Int,
        status: String?,
        method: String?
    ): Result<PaymentHistory> = withContext(ioDispatcher) {
        runCatching {
            val response = paymentApi.getPaymentRecords(studentId, page, size, status, method)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to load records")
            }
            val data = response.data ?: JsonObject(emptyMap())
            val recordsArray = data.jsonArrayOrNull("content")
                ?: data.jsonArrayOrNull("records")
                ?: response.data?.asJsonArrayOrNull()
            val records = recordsArray?.mapNotNull { it.asJsonObjectOrNull()?.toPaymentRecord() } ?: emptyList()
            val total = data.intOrNull("totalElements")
                ?: data.intOrNull("total")
                ?: records.size
            PaymentHistory(records = records, total = total)
        }
    }

    private fun JsonObject.toPaymentRecord(): PaymentRecord? {
        val id = longOrNull("id") ?: return null
        val amount = doubleOrNull("amount") ?: 0.0
        return PaymentRecord(
            id = id,
            amount = amount,
            status = stringOrNull("status"),
            method = stringOrNull("method"),
            type = stringOrNull("type"),
            createdAt = stringOrNull("createTime") ?: stringOrNull("createdAt"),
            updatedAt = stringOrNull("updateTime") ?: stringOrNull("updatedAt"),
            description = stringOrNull("description") ?: stringOrNull("remark")
        )
    }
}
