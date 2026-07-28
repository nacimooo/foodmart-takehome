package com.example.foodmart.domain.repository

import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    val items: Flow<List<CartItem>>

    fun add(item: FoodItem)
    fun remove(foodItemUuid: String)
    fun clear()
}
