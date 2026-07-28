package com.example.foodmart.data.repository

import com.example.foodmart.cartItem
import com.example.foodmart.data.remote.PurchaseApi
import com.example.foodmart.data.remote.dto.PurchaseRequestDto
import com.example.foodmart.data.remote.dto.PurchaseResponseDto
import com.example.foodmart.foodItem
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PurchaseRepositoryImplTest {

    private val purchaseApi: PurchaseApi = mockk()
    private val repository = PurchaseRepositoryImpl(purchaseApi)

    @Test
    fun `builds the request from cart items with uuids and quantities`() = runTest {
        val requestSlot = slot<PurchaseRequestDto>()
        coEvery { purchaseApi.purchase(capture(requestSlot)) } returns
            PurchaseResponseDto(orderUuid = "order-1", status = "completed")

        repository.purchase(
            listOf(
                cartItem(item = foodItem(uuid = "item-1"), quantity = 2),
                cartItem(item = foodItem(uuid = "item-2"), quantity = 1),
            ),
        )

        val sent = requestSlot.captured.items
        assertEquals(listOf("item-1", "item-2"), sent.map { it.foodItemUuid })
        assertEquals(listOf(2, 1), sent.map { it.quantity })
    }
}
