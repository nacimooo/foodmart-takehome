package com.example.foodmart.domain.usecase

import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.repository.CartRepository
import com.example.foodmart.domain.repository.PurchaseRepository
import javax.inject.Inject


// Purchases all items in the cart and clear the cart on success.
// If the purchase fails, the cart is left untouched so the user can retry.
class PurchaseCartUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(items: List<CartItem>) {
        purchaseRepository.purchase(items)
        cartRepository.clear()
    }
}
