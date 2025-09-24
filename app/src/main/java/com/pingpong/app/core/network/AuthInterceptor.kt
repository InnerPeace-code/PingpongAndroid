package com.pingpong.app.core.network

import com.pingpong.app.core.auth.TokenProvider
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenProvider.currentToken()
        val newRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("X-Token", token)
                .build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}
