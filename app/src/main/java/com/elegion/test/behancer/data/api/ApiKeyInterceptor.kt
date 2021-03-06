package com.elegion.test.behancer.data.api

import com.elegion.test.behancer.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        val httpUrl = request.url.newBuilder()
            .addQueryParameter("client_id", BuildConfig.API_KEY)
            .build()
        request = request.newBuilder().url(httpUrl).build()

        return chain.proceed(request)
    }
}