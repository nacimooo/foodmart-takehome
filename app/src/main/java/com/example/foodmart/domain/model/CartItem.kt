package com.example.foodmart.domain.model

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int,
) {
    val lineTotal: Double
        get() = foodItem.price * quantity
}
