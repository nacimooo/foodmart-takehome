package com.example.foodmart.ui.foodlist

import app.cash.turbine.test
import com.example.foodmart.MainDispatcherRule
import com.example.foodmart.cartItem
import com.example.foodmart.domain.usecase.AddToCartUseCase
import com.example.foodmart.domain.usecase.GetCategoriesUseCase
import com.example.foodmart.domain.usecase.GetFoodItemsUseCase
import com.example.foodmart.domain.usecase.ObserveCartUseCase
import com.example.foodmart.foodItem
import com.example.foodmart.meatCategory
import com.example.foodmart.produceCategory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FoodListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getFoodItems: GetFoodItemsUseCase = mockk()
    private val getCategories: GetCategoriesUseCase = mockk()
    private val addToCart: AddToCartUseCase = mockk(relaxed = true)
    private val observeCart: ObserveCartUseCase = mockk()

    private val cartFlow = MutableStateFlow(emptyList<com.example.foodmart.domain.model.CartItem>())

    private val cheapProduce = foodItem(uuid = "item-1", name = "Apple", price = 0.99)
    private val midProduce = foodItem(uuid = "item-2", name = "Grapes", price = 4.99)
    private val expensiveMeat =
        foodItem(uuid = "item-3", name = "Steak", price = 16.49, category = meatCategory)

    private fun createViewModel(): FoodListViewModel {
        every { observeCart() } returns cartFlow
        return FoodListViewModel(getFoodItems, getCategories, addToCart, observeCart)
    }

    private fun givenItemsLoadSuccessfully() {
        coEvery { getFoodItems() } returns listOf(midProduce, cheapProduce, expensiveMeat)
        coEvery { getCategories() } returns listOf(produceCategory, meatCategory)
    }

    @Test
    fun `loads items and categories on init`() = runTest {
        givenItemsLoadSuccessfully()

        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(3, state.visibleItems.size)
        assertEquals(2, state.categories.size)
    }

    @Test
    fun `shows error when loading fails and retry recovers`() = runTest {
        coEvery { getFoodItems() } throws IOException("network down")
        coEvery { getCategories() } returns listOf(produceCategory)

        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.visibleItems.isEmpty())

        givenItemsLoadSuccessfully()
        viewModel.load()

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(3, viewModel.uiState.value.visibleItems.size)
    }

    @Test
    fun `sorts items by ascending price`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onSortOrderSelected(SortOrder.PRICE_ASCENDING)

        assertEquals(
            listOf("Apple", "Grapes", "Steak"),
            viewModel.uiState.value.visibleItems.map { it.name },
        )
    }

    @Test
    fun `sorts items by descending price`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onSortOrderSelected(SortOrder.PRICE_DESCENDING)

        assertEquals(
            listOf("Steak", "Grapes", "Apple"),
            viewModel.uiState.value.visibleItems.map { it.name },
        )
    }

    @Test
    fun `reselecting the active sort clears it`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onSortOrderSelected(SortOrder.PRICE_ASCENDING)
        viewModel.onSortOrderSelected(SortOrder.PRICE_ASCENDING)

        assertEquals(SortOrder.NONE, viewModel.uiState.value.sortOrder)
    }

    @Test
    fun `filters items by a single category`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onCategoryToggled(meatCategory.uuid)

        assertEquals(listOf("Steak"), viewModel.uiState.value.visibleItems.map { it.name })
    }

    @Test
    fun `filters items by multiple categories`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onCategoryToggled(meatCategory.uuid)
        viewModel.onCategoryToggled(produceCategory.uuid)

        assertEquals(3, viewModel.uiState.value.visibleItems.size)
    }

    @Test
    fun `untoggling a category removes the filter`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onCategoryToggled(meatCategory.uuid)
        viewModel.onCategoryToggled(meatCategory.uuid)

        assertEquals(3, viewModel.uiState.value.visibleItems.size)
    }

    @Test
    fun `filter and sort combine`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onCategoryToggled(produceCategory.uuid)
        viewModel.onSortOrderSelected(SortOrder.PRICE_DESCENDING)

        assertEquals(
            listOf("Grapes", "Apple"),
            viewModel.uiState.value.visibleItems.map { it.name },
        )
    }

    @Test
    fun `add to cart delegates to use case`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.onAddToCart(cheapProduce)

        verify(exactly = 1) { addToCart(cheapProduce) }
    }

    @Test
    fun `cart item count sums quantities`() = runTest {
        givenItemsLoadSuccessfully()
        val viewModel = createViewModel()

        viewModel.cartItemCount.test {
            assertEquals(0, awaitItem())

            cartFlow.value = listOf(
                cartItem(item = cheapProduce, quantity = 2),
                cartItem(item = expensiveMeat, quantity = 3),
            )

            assertEquals(5, awaitItem())
        }
    }
}
