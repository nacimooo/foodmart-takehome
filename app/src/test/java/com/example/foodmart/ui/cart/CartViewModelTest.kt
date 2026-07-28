package com.example.foodmart.ui.cart

import app.cash.turbine.test
import com.example.foodmart.MainDispatcherRule
import com.example.foodmart.cartItem
import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.usecase.ObserveCartUseCase
import com.example.foodmart.domain.usecase.PurchaseCartUseCase
import com.example.foodmart.domain.usecase.RemoveFromCartUseCase
import com.example.foodmart.foodItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCart: ObserveCartUseCase = mockk()
    private val removeFromCart: RemoveFromCartUseCase = mockk(relaxed = true)
    private val purchaseCart: PurchaseCartUseCase = mockk()

    private val cartFlow = MutableStateFlow(emptyList<CartItem>())

    private val apple = foodItem(uuid = "item-1", name = "Apple", price = 1.00)
    private val steak = foodItem(uuid = "item-2", name = "Steak", price = 16.50)

    private fun createViewModel(): CartViewModel {
        every { observeCart() } returns cartFlow
        return CartViewModel(observeCart, removeFromCart, purchaseCart)
    }

    @Test
    fun `state reflects cart items and total price`() = runTest {
        cartFlow.value = listOf(
            cartItem(item = apple, quantity = 3),
            cartItem(item = steak, quantity = 1),
        )
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.items.size)
            assertEquals(4, state.items.sumOf { it.quantity })
            assertEquals(19.50, state.totalPrice, 0.001)
        }
    }

    @Test
    fun `remove delegates to use case`() = runTest {
        val viewModel = createViewModel()

        viewModel.onRemoveItem("item-1")

        verify(exactly = 1) { removeFromCart("item-1") }
    }

    @Test
    fun `successful purchase shows confirmation message`() = runTest {
        val items = listOf(cartItem(item = apple, quantity = 2))
        cartFlow.value = items
        coEvery { purchaseCart(items) } answers { cartFlow.value = emptyList() }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPurchase()

            val state = expectMostRecentItem()
            assertEquals("Order placed successfully!", state.userMessage)
            assertFalse(state.isPurchasing)
            assertEquals(0, state.items.size)
        }
        coVerify(exactly = 1) { purchaseCart(items) }
    }

    @Test
    fun `failed purchase keeps items and shows error message`() = runTest {
        val items = listOf(cartItem(item = apple, quantity = 2))
        cartFlow.value = items
        coEvery { purchaseCart(any()) } throws IOException("server down")
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPurchase()

            val state = expectMostRecentItem()
            assertEquals("Purchase failed. Please try again.", state.userMessage)
            assertFalse(state.isPurchasing)
            assertEquals(1, state.items.size)
        }
    }

    @Test
    fun `purchase with empty cart does nothing`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPurchase()
        }

        coVerify(exactly = 0) { purchaseCart(any()) }
    }

    @Test
    fun `message is cleared after being shown`() = runTest {
        val items = listOf(cartItem(item = apple))
        cartFlow.value = items
        coEvery { purchaseCart(items) } answers { cartFlow.value = emptyList() }
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onPurchase()
            viewModel.onUserMessageShown()

            assertEquals(null, expectMostRecentItem().userMessage)
        }
    }
}
