package com.pingpong.app.core.data

import com.pingpong.app.core.auth.TokenProvider
import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.model.TokenInfo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class SessionRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenProvider: TokenProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val mutex = Mutex()
    @Volatile
    private var cached: TokenInfo? = null

    suspend fun currentSession(forceRefresh: Boolean = false): TokenInfo = withContext(ioDispatcher) {
        mutex.withLock {
            if (!forceRefresh) {
                cached?.let { return@withLock it }
            }
            val token = tokenProvider.tokenFlow.first()
                ?: throw IllegalStateException("Session token not available")
            val response = authRepository.fetchTokenInfo(token)
            if (response.code != 20000 || response.data == null) {
                throw IllegalStateException(response.message ?: "Unable to fetch session info")
            }
            cached = response.data
            cached!!
        }
    }

    suspend fun refresh(): TokenInfo = currentSession(forceRefresh = true)

    fun invalidate() {
        cached = null
    }
}
