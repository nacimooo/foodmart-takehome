package com.example.foodmart.domain.usecase

import com.example.foodmart.domain.model.FoodItem
import com.example.foodmart.domain.repository.FoodRepository
import javax.inject.Inject

class GetFoodItemsUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(): List<FoodItem> = foodRepository.getFoodItems()
}
