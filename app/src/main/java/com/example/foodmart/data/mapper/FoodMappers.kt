package com.example.foodmart.data.mapper

import com.example.foodmart.data.remote.dto.FoodCategoryDto
import com.example.foodmart.data.remote.dto.FoodItemDto
import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.model.FoodItem

fun FoodCategoryDto.toDomain(): Category = Category(
    uuid = uuid,
    name = name,
)

fun FoodItemDto.toDomain(category: FoodCategoryDto?): FoodItem = FoodItem(
    uuid = uuid,
    name = name,
    price = price,
    category = category?.toDomain() ?: Category(uuid = categoryUuid, name = "Uncategorized"),
    imageUrl = imageUrl,
)
