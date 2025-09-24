package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.booleanOrNull
import com.pingpong.app.core.common.intOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.jsonObjectOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.coach.CoachAccountSnapshot
import com.pingpong.app.core.model.coach.CoachApplication
import com.pingpong.app.core.model.coach.CoachStudent
import com.pingpong.app.core.model.coach.CoachStudentDetail
import com.pingpong.app.core.model.coach.CoachTransaction
import com.pingpong.app.core.model.coach.CoachTransactionPage
import com.pingpong.app.core.network.api.CoachApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Singleton
class CoachRepository @Inject constructor(
    private val coachApi: CoachApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getStudentApplications(coachId: Long): Result<List<CoachApplication>> = withContext(ioDispatcher) {
        runCatching {
            val response = coachApi.getStudentApplications(coachId)
            ensureSuccess(response)
            response.data.parseApplications()
        }
    }

    suspend fun reviewApplication(applicationId: Long, accept: Boolean): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = coachApi.reviewStudentSelect(applicationId, accept)
            ensureSuccess(response)
        }
    }

    suspend fun getStudentDetail(studentId: Long): Result<CoachStudentDetail> = withContext(ioDispatcher) {
        runCatching {
            val response = coachApi.getStudentDetail(studentId)
            ensureSuccess(response)
            val data = response.data?.asJsonObjectOrNull()
                ?: response.data?.jsonObjectOrNull("data")
                ?: throw IllegalStateException("Student detail not found")
            data.toStudentDetail()
        }
    }

    suspend fun getRelatedStudents(coachId: Long): Result<List<CoachStudent>> = withContext(ioDispatcher) {
        runCatching {
            val response = coachApi.getRelatedStudents(coachId)
            ensureSuccess(response)
            response.data.parseStudents()
        }
    }

    suspend fun getAccountSnapshot(
        coachId: Long,
        page: Int,
        size: Int,
        type: String?
    ): Result<CoachAccountSnapshot> = withContext(ioDispatcher) {
        runCatching {
            val balanceResponse = coachApi.getCoachBalance(coachId)
            ensureSuccess(balanceResponse)
            val balance = balanceResponse.data.extractBalance()
            val transactionResponse = coachApi.getCoachTransactions(coachId, page, size, type)
            ensureSuccess(transactionResponse)
            val pageResult = transactionResponse.data.parseTransactions(page, size)
            CoachAccountSnapshot(balance = balance, transactions = pageResult)
        }
    }

    suspend fun submitWithdraw(
        coachId: Long,
        amount: Double,
        bankAccount: String,
        bankName: String,
        accountHolder: String
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = coachApi.withdraw(coachId, amount, bankAccount, bankName, accountHolder)
            ensureSuccess(response)
        }
    }

    private fun ensureSuccess(response: com.pingpong.app.core.model.ApiResponse<*>?) {
        if (response == null || response.code != 20000) {
            throw IllegalStateException(response?.message ?: "Request failed")
        }
    }

    private fun JsonElement?.parseApplications(): List<CoachApplication> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.let { obj ->
                obj.jsonArrayOrNull("data")
                    ?: obj.jsonArrayOrNull("list")
                    ?: obj.jsonArrayOrNull("records")
            }
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val relation = obj.jsonObjectOrNull("relation") ?: obj
            val student = obj.jsonObjectOrNull("student") ?: obj.jsonObjectOrNull("studentInfo")
            val coach = obj.jsonObjectOrNull("coach") ?: obj.jsonObjectOrNull("coachInfo")
            val relationId = relation.longOrNull("id")
                ?: relation.longOrNull("relationId")
                ?: obj.longOrNull("relationId")
                ?: obj.longOrNull("id")
                ?: return@mapNotNull null
            CoachApplication(
                relationId = relationId,
                coachId = coach?.longOrNull("id") ?: relation.longOrNull("coachId") ?: obj.longOrNull("coachId"),
                coachName = coach?.stringOrNull("realName") ?: coach?.stringOrNull("name"),
                coachMale = coach?.booleanOrNull("male") ?: coach?.booleanOrNull("isMale"),
                coachAge = coach?.intOrNull("age"),
                studentId = student?.longOrNull("id") ?: relation.longOrNull("studentId"),
                studentName = student?.stringOrNull("name") ?: student?.stringOrNull("realName") ?: obj.stringOrNull("studentName"),
                studentMale = student?.booleanOrNull("male") ?: student?.booleanOrNull("isMale"),
                studentAge = student?.intOrNull("age"),
                status = relation.stringOrNull("status") ?: obj.stringOrNull("status"),
                appliedAt = relation.stringOrNull("createTime")
                    ?: relation.stringOrNull("createdAt")
                    ?: obj.stringOrNull("createTime")
            )
        }
    }

    private fun JsonElement?.parseStudents(): List<CoachStudent> {
        val array = this.asJsonArrayOrNull()
            ?: this.asJsonObjectOrNull()?.let { obj ->
                obj.jsonArrayOrNull("data")
                    ?: obj.jsonArrayOrNull("list")
                    ?: obj.jsonArrayOrNull("records")
            }
            ?: emptyList()
        return array.mapNotNull { element ->
            val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = obj.longOrNull("id") ?: obj.longOrNull("studentId") ?: return@mapNotNull null
            CoachStudent(
                id = id,
                name = obj.stringOrNull("name") ?: obj.stringOrNull("realName") ?: obj.stringOrNull("studentName"),
                male = obj.booleanOrNull("male") ?: obj.booleanOrNull("isMale"),
                age = obj.intOrNull("age"),
                phone = obj.stringOrNull("phone") ?: obj.stringOrNull("mobile"),
                email = obj.stringOrNull("email"),
                schoolId = obj.longOrNull("schoolId"),
                schoolName = obj.stringOrNull("schoolName") ?: obj.stringOrNull("campusName")
            )
        }
    }

    private fun JsonObject.toStudentDetail(): CoachStudentDetail {
        val baseId = longOrNull("id") ?: longOrNull("studentId")
            ?: throw IllegalStateException("Missing student id")
        return CoachStudentDetail(
            id = baseId,
            username = stringOrNull("username") ?: stringOrNull("account") ?: stringOrNull("userName"),
            name = stringOrNull("name") ?: stringOrNull("realName") ?: stringOrNull("studentName"),
            male = booleanOrNull("male") ?: booleanOrNull("isMale"),
            age = intOrNull("age"),
            phone = stringOrNull("phone") ?: stringOrNull("mobile"),
            email = stringOrNull("email"),
            avatar = stringOrNull("avatar") ?: stringOrNull("avatarUrl"),
            schoolId = longOrNull("schoolId"),
            schoolName = stringOrNull("schoolName") ?: stringOrNull("campusName")
        )
    }

    private fun JsonElement?.extractBalance(): Double {
        return when (this) {
            is JsonPrimitive -> this.doubleOrNull ?: this.contentOrNull?.toDoubleOrNull() ?: 0.0
            is JsonObject -> parseBalanceFromObject(this)
            else -> this?.asJsonObjectOrNull()?.let { parseBalanceFromObject(it) } ?: 0.0
        }
    }

    private fun parseBalanceFromObject(obj: JsonObject): Double {
        val direct = obj["balance"] ?: obj["amount"]
        val primitiveValue = (direct as? JsonPrimitive)?.let { it.doubleOrNull ?: it.contentOrNull?.toDoubleOrNull() }
        return primitiveValue
            ?: obj.stringOrNull("balance")?.toDoubleOrNull()
            ?: obj.stringOrNull("amount")?.toDoubleOrNull()
            ?: 0.0
    }

    private fun JsonElement?.parseTransactions(page: Int, size: Int): CoachTransactionPage {
        val obj = this.asJsonObjectOrNull()
        val arrayCandidate = when {
            this.asJsonArrayOrNull() != null -> this.asJsonArrayOrNull()
            obj?.jsonArrayOrNull("records") != null -> obj.jsonArrayOrNull("records")
            obj?.jsonArrayOrNull("data") != null -> obj.jsonArrayOrNull("data")
            obj?.jsonArrayOrNull("content") != null -> obj.jsonArrayOrNull("content")
            else -> null
        }
        val entries = arrayCandidate ?: emptyList<JsonElement>()
        val records = entries.mapNotNull { element ->
            val transactionObj = element.asJsonObjectOrNull() ?: return@mapNotNull null
            val id = transactionObj.longOrNull("id") ?: return@mapNotNull null
            val amount = transactionObj["amount"]
                ?.let { (it as? JsonPrimitive)?.let { primitive -> primitive.doubleOrNull ?: primitive.contentOrNull?.toDoubleOrNull() } }
                ?: transactionObj.stringOrNull("amount")?.toDoubleOrNull()
                ?: 0.0
                ?: transactionObj.stringOrNull("amount")?.toDoubleOrNull()
                ?: 0.0
            CoachTransaction(
                id = id,
                amount = amount,
                type = transactionObj.stringOrNull("type") ?: transactionObj.stringOrNull("transactionType"),
                description = transactionObj.stringOrNull("description") ?: transactionObj.stringOrNull("remark"),
                createdAt = transactionObj.stringOrNull("createTime") ?: transactionObj.stringOrNull("createdAt"),
                status = transactionObj.stringOrNull("status")
            )
        }
        val total = obj?.intOrNull("total")
            ?: obj?.intOrNull("totalElements")
            ?: obj?.intOrNull("totalCount")
            ?: records.size
        val resolvedPage = obj?.intOrNull("page") ?: obj?.intOrNull("current") ?: page
        val resolvedSize = obj?.intOrNull("size") ?: obj?.intOrNull("pageSize") ?: size
        return CoachTransactionPage(
            records = records,
            total = total,
            page = resolvedPage,
            size = resolvedSize
        )
    }
}


