package com.example.foodmart.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodCategoryDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
)
