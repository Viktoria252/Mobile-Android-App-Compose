package com.mobile.vedroid.compose.network

import com.google.gson.annotations.SerializedName

data class ItemRequest (
    val id: String,
    @SerializedName("title")
    val name: String,
    val category: String,
    val description: String,
    @SerializedName("dailyprice")
    val pricePerDay: Double,
    @SerializedName("image")
    val imageRes: String? = null) {}