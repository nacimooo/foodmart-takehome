package com.example.foodmart.data.repository

import com.example.foodmart.data.remote.PurchaseApi
import com.example.foodmart.data.remote.dto.PurchaseItemDto
import com.example.foodmart.data.remote.dto.PurchaseRequestDto
import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.repository.PurchaseRepository
import javax.inject.Inject

class PurchaseRepositoryImpl @Inject constructor(
    private val purchaseApi: PurchaseApi,
) : PurchaseRepository {

    override suspend fun purchase(items: List<CartItem>) {
        val request = PurchaseRequestDto(
            items = items.map {
                PurchaseItemDto(foodItemUuid = it.foodItem.uuid, quantity = it.quantity)
            },
        )
        purchaseApi.purchase(request)
    }
}
