package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.common.asJsonArrayOrNull
import com.pingpong.app.core.common.asJsonObjectOrNull
import com.pingpong.app.core.common.longOrNull
import com.pingpong.app.core.common.jsonArrayOrNull
import com.pingpong.app.core.common.stringOrNull
import com.pingpong.app.core.model.student.NotificationItem
import com.pingpong.app.core.network.api.NotificationApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationApi: NotificationApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getUnreadNotifications(userId: Long, userType: String): Result<List<NotificationItem>> = withContext(ioDispatcher) {
        runCatching {
            val response = notificationApi.getUnreadNotifications(userId, userType)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to load notifications")
            }
            val data = response.data
            val array = data.asJsonArrayOrNull()
                ?: data.asJsonObjectOrNull()?.jsonArrayOrNull("data")
                ?: emptyList()
            array.mapNotNull { element ->
                val obj = element.asJsonObjectOrNull() ?: return@mapNotNull null
                val id = obj.longOrNull("id") ?: return@mapNotNull null
                NotificationItem(
                    id = id,
                    title = obj.stringOrNull("title") ?: obj.stringOrNull("subject"),
                    content = obj.stringOrNull("content") ?: obj.stringOrNull("message"),
                    createdAt = obj.stringOrNull("createTime") ?: obj.stringOrNull("createdAt"),
                    read = obj.stringOrNull("status")?.equals("READ", ignoreCase = true) == true
                )
            }
        }
    }

    suspend fun markAsRead(notificationId: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val response = notificationApi.markAsRead(notificationId)
            if (response.code != 20000) {
                throw IllegalStateException(response.message ?: "Failed to mark as read")
            }
        }
    }
}
