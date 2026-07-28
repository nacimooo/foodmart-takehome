package com.example.foodmart.data.repository

import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.model.FoodItem
import com.example.foodmart.domain.repository.CartRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {

    private val itemsByUuid = MutableStateFlow<Map<String, CartItem>>(emptyMap())

    override val items: Flow<List<CartItem>> = itemsByUuid.map { it.values.toList() }

    override fun add(item: FoodItem) {
        itemsByUuid.update { current ->
            val existing = current[item.uuid]
            val updated = existing?.copy(quantity = existing.quantity + 1)
                ?: CartItem(foodItem = item, quantity = 1)
            current + (item.uuid to updated)
        }
    }

    override fun remove(foodItemUuid: String) {
        itemsByUuid.update { it - foodItemUuid }
    }

    override fun clear() {
        itemsByUuid.value = emptyMap()
    }
}
