package com.mobile.vedroid.compose

import kotlinx.serialization.Serializable

sealed class MainActivityRoutes{
    @Serializable
    data class Start (val name: String? = null, val email: String? = null) {
    }

    @Serializable
    object Registration

    @Serializable
    object Settings

    @Serializable
    object Content
}