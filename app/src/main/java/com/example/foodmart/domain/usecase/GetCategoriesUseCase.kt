package com.example.foodmart.domain.usecase

import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.repository.FoodRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(): List<Category> = foodRepository.getCategories()
}
