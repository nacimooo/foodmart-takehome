package com.example.foodmart.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodItemDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
    @SerialName("price") val price: Double,
    @SerialName("category_uuid") val categoryUuid: String,
    @SerialName("image_url") val imageUrl: String,
)
