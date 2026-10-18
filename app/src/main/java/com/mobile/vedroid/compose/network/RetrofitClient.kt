package com.mobile.vedroid.compose.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val BASE_URL = "http://10.18.51.127:8085/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ItemRetrofitAPI by lazy {
        retrofit.create(ItemRetrofitAPI::class.java)
    }
}