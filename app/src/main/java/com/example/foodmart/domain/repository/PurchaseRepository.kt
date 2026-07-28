package com.example.foodmart.domain.repository

import com.example.foodmart.domain.model.CartItem

interface PurchaseRepository {
    suspend fun purchase(items: List<CartItem>)
}
