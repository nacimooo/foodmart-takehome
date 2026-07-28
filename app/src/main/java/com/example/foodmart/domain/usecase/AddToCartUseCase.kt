package com.example.foodmart.domain.usecase

import com.example.foodmart.domain.model.FoodItem
import com.example.foodmart.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(item: FoodItem) = cartRepository.add(item)
}
