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

        private lateinit var sClient: OkHttpClient
        private lateinit var sRetrofit: Retrofit
        private lateinit var sGson: Gson
        private lateinit var sApi: BehanceApi

        private fun getClient(): OkHttpClient {

            val builder = OkHttpClient.Builder()
            builder.addInterceptor(ApiKeyInterceptor())
            if (!BuildConfig.BUILD_TYPE.contains("release")) {
                builder.addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(
                            HttpLoggingInterceptor
                                .Level.BODY
                        )
                )
            }
            sClient = builder.build()

            return sClient
        }


        private fun getRetrofit(): Retrofit {

            sGson = Gson()
            sRetrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(sGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return sRetrofit
        }

        fun getApiService(): BehanceApi {
            sApi = getRetrofit().create(BehanceApi::class.java)
            return sApi
        }
    }
}