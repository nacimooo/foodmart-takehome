package com.example.foodmart.ui.cart

import com.example.foodmart.domain.model.CartItem

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val isPurchasing: Boolean = false,
    val userMessage: String? = null,
) {
    val totalPrice: Double
        get() = items.sumOf { it.lineTotal }
}
