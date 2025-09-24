package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asDoubleOrNull
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.asStringOrNull
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
            response.data?.asDoubleOrNull() ?: response.data?.asStringOrNull()?.toDoubleOrNull() ?: 0.0
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
            val dataObj = response.data?.asJsonObjectOrNull()
                ?: throw IllegalStateException("Missing payment info")
            PaymentSummary(
                recordId = dataObj.longOrNull("id") ?: dataObj.longOrNull("recordId")
                    ?: throw IllegalStateException("Missing payment record id"),
                qrCodeUrl = dataObj.stringOrNull("qrCodeUrl")
                    ?: dataObj.stringOrNull("qrcode")
                    ?: dataObj.stringOrNull("qrCode")
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
            val root = response.data?.asJsonObjectOrNull()
            val recordsArray = root?.jsonArrayOrNull("content")
                ?: root?.jsonArrayOrNull("records")
                ?: response.data?.asJsonArrayOrNull()
            val records = recordsArray?.mapNotNull { element ->
                element.asJsonObjectOrNull()?.toPaymentRecord()
            } ?: emptyList()
            val total = root?.intOrNull("totalElements")
                ?: root?.intOrNull("total")
                ?: root?.intOrNull("count")
                ?: records.size
            PaymentHistory(records = records, total = total)
        }
    }

    private fun JsonObject.toPaymentRecord(): PaymentRecord? {
        val id = longOrNull("id") ?: longOrNull("recordId") ?: return null
        val amount = doubleOrNull("amount") ?: stringOrNull("amount")?.toDoubleOrNull() ?: 0.0
        return PaymentRecord(
            id = id,
            amount = amount,
            status = stringOrNull("status"),
            method = stringOrNull("method") ?: stringOrNull("paymentMethod"),
            type = stringOrNull("type"),
            createdAt = stringOrNull("createTime") ?: stringOrNull("createdAt"),
            updatedAt = stringOrNull("updateTime") ?: stringOrNull("updatedAt"),
            description = stringOrNull("description") ?: stringOrNull("remark")
        )
    }
}
