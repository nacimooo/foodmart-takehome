package com.example.foodmart.domain.repository

import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.model.FoodItem

interface FoodRepository {
    suspend fun getFoodItems(): List<FoodItem>
    suspend fun getCategories(): List<Category>
}
