package com.silab.data.api

import com.silab.utils.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            // Interceptor untuk menambahkan header Accept: application/json
            val headerInterceptor = okhttp3.Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(60, SECONDS)   // Set connection timeout
                .readTimeout(60, SECONDS)      // Set read timeout
                .writeTimeout(60, SECONDS)     // Set write timeout
                .addInterceptor(headerInterceptor)    // Tambahkan header interceptor
                .addInterceptor(loggingInterceptor)                // Add logging interceptor
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(Utils.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}