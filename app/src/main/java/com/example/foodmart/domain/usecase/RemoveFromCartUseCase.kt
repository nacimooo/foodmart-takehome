package com.example.foodmart.domain.usecase

import com.example.foodmart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(foodItemUuid: String) = cartRepository.remove(foodItemUuid)
}
