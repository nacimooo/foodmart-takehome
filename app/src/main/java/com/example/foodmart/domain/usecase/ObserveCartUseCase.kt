package com.example.foodmart.domain.usecase

import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.repository.CartRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(): Flow<List<CartItem>> = cartRepository.items
}
