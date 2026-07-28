package com.example.foodmart.domain.usecase

import com.example.foodmart.cartItem
import com.example.foodmart.domain.repository.CartRepository
import com.example.foodmart.domain.repository.PurchaseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Test

class PurchaseCartUseCaseTest {

    private val purchaseRepository: PurchaseRepository = mockk()
    private val cartRepository: CartRepository = mockk(relaxed = true)
    private val useCase = PurchaseCartUseCase(purchaseRepository, cartRepository)

    @Test
    fun `clears the cart after a successful purchase`() = runTest {
        val items = listOf(cartItem())
        coEvery { purchaseRepository.purchase(items) } returns Unit

        useCase(items)

        coVerify(exactly = 1) { purchaseRepository.purchase(items) }
        verify(exactly = 1) { cartRepository.clear() }
    }

    @Test
    fun `keeps the cart when the purchase fails`() = runTest {
        val items = listOf(cartItem())
        coEvery { purchaseRepository.purchase(items) } throws IOException("server down")

        assertThrows(IOException::class.java) {
            kotlinx.coroutines.runBlocking { useCase(items) }
        }

        verify(exactly = 0) { cartRepository.clear() }
    }
}
