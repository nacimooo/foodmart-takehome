package com.example.foodmart.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseRequestDto(
    @SerialName("items") val items: List<PurchaseItemDto>,
)

@Serializable
data class PurchaseItemDto(
    @SerialName("food_item_uuid") val foodItemUuid: String,
    @SerialName("quantity") val quantity: Int,
)

@Serializable
data class PurchaseResponseDto(
    @SerialName("order_uuid") val orderUuid: String,
    @SerialName("status") val status: String,
    // null until implmeented
    @SerialName("total_price") val totalPrice: Double? = null,
)
