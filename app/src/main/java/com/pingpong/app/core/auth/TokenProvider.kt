package com.pingpong.app.core.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Singleton
class TokenProvider @Inject constructor(
    private val tokenManager: TokenManager
) {
    fun currentToken(): String? = runBlocking { tokenManager.tokenFlow.first() }
    fun currentRole(): String? = runBlocking { tokenManager.roleFlow.first() }
    val tokenFlow: Flow<String?> = tokenManager.tokenFlow
}
