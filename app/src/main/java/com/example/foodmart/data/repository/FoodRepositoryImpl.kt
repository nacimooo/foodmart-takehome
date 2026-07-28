package com.example.foodmart.data.repository

import com.example.foodmart.data.mapper.toDomain
import com.example.foodmart.data.remote.FoodMartApi
import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.model.FoodItem
import com.example.foodmart.domain.repository.FoodRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FoodRepositoryImpl @Inject constructor(
    private val api: FoodMartApi,
) : FoodRepository {

    override suspend fun getFoodItems(): List<FoodItem> = coroutineScope {
        val itemsDeferred = async { api.getFoodItems() }
        val categoriesDeferred = async { api.getFoodCategories() }

        val categoriesByUuid = categoriesDeferred.await().associateBy { it.uuid }
        itemsDeferred.await().map { it.toDomain(categoriesByUuid[it.categoryUuid]) }
    }

    override suspend fun getCategories(): List<Category> =
        api.getFoodCategories().map { it.toDomain() }
}
