package com.elegion.test.behancer.utils

import com.elegion.test.behancer.BuildConfig
import com.elegion.test.behancer.data.api.ApiKeyInterceptor
import com.elegion.test.behancer.data.api.BehanceApi
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiUtils {

    companion object {
        val NETWORK_EXCEPTIONS = listOf(
            UnknownHostException::class,
            SocketTimeoutException::class,
            ConnectException::class
        )

        private fun getClient(): OkHttpClient = OkHttpClient.Builder()
            .setupInterceptors()
            .build()

        private fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        fun getApiService(): BehanceApi {
            return getRetrofit().create(BehanceApi::class.java)
        }

        private fun OkHttpClient.Builder.setupInterceptors() = apply {
            addInterceptor(ApiKeyInterceptor())
            if (!BuildConfig.BUILD_TYPE.contains("release")) {
                addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
            }
        }
    }
}