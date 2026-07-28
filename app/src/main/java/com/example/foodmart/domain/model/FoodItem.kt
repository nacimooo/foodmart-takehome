package com.example.foodmart.domain.model

data class FoodItem(
    val uuid: String,
    val name: String,
    val price: Double,
    val category: Category,
    val imageUrl: String,
)
