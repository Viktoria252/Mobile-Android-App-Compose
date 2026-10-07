package com.mobile.vedroid.compose.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemRetrofitAPI {

    @GET("api/equipments/all")
    fun getItem(): Call<List<ItemRequest>>

    @GET("api/equipments/all")
    fun getItems(): Call<List<ItemRequest>>

    @GET("api/equipments?amount={count}}")
    fun getItems(@Path("count") count: Int): Call<List<ItemRequest>>
}