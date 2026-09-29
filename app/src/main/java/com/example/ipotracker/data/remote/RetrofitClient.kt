package com.example.ipotracker.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private class ApiSecurityInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("User-Agent", "IPOTrackerIndia-Android/1.0")

            // Safely inject API key if provided by BuildConfig or secrets panel.
            // NEVER hardcoded in source code!
            val apiKey = IpoConfig.getApiKey()
            if (apiKey.isNotEmpty()) {
                requestBuilder.header("X-API-KEY", apiKey)
                requestBuilder.header("Authorization", "Bearer $apiKey")
            }

            return chain.proceed(requestBuilder.build())
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(ApiSecurityInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(IpoConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(IpoConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(IpoConfig.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun createIpoApiService(baseUrl: String = IpoConfig.API_BASE_URL): IpoApiService {
        val cleanBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(cleanBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(IpoApiService::class.java)
    }
}
