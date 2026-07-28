package com.example.foodmart.data.remote

import com.example.foodmart.data.remote.dto.FoodCategoryDto
import com.example.foodmart.data.remote.dto.FoodItemDto
import retrofit2.http.GET

interface FoodMartApi {

    @GET("api/food_items.json")
    suspend fun getFoodItems(): List<FoodItemDto>

    @GET("api/food_item_categories.json")
    suspend fun getFoodCategories(): List<FoodCategoryDto>

    companion object {
        const val BASE_URL = "https://7shifts.github.io/mobile-takehome/"
    }
}
