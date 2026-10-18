package com.mobile.vedroid.compose.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItemRetrofitAPI {

    @GET("api/equipments/all")
    fun getItems(): Call<List<ItemRequest>>

    @GET("api/equipments")
    suspend fun getItems(
        @Query("amount") amount: Int,
        @Query("offset") offset: Int
    ): List<ItemRequest>
}