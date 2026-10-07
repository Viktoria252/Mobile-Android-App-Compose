package com.mobile.vedroid.compose.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val BASE_URL = "http://10.18.51.127:8085/"

    val apiService: ItemRetrofitAPI
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItemRetrofitAPI::class.java)
}