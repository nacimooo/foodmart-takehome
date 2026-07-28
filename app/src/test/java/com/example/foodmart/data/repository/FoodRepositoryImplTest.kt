package com.example.foodmart.data.repository

import com.example.foodmart.data.remote.FoodMartApi
import com.example.foodmart.data.remote.dto.FoodCategoryDto
import com.example.foodmart.data.remote.dto.FoodItemDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FoodRepositoryImplTest {

    private val api: FoodMartApi = mockk()
    private val repository = FoodRepositoryImpl(api)

    private val produceDto = FoodCategoryDto(uuid = "cat-produce", name = "Produce")
    private val meatDto = FoodCategoryDto(uuid = "cat-meat", name = "Meat")

    private fun itemDto(
        uuid: String,
        name: String,
        price: Double,
        categoryUuid: String,
    ) = FoodItemDto(
        uuid = uuid,
        name = name,
        price = price,
        categoryUuid = categoryUuid,
        imageUrl = "https://example.com/$uuid.png",
    )

    @Test
    fun `maps items and joins category names by uuid`() = runTest {
        coEvery { api.getFoodItems() } returns listOf(
            itemDto("item-1", "Apple", 0.99, "cat-produce"),
            itemDto("item-2", "Steak", 16.49, "cat-meat"),
        )
        coEvery { api.getFoodCategories() } returns listOf(produceDto, meatDto)

        val items = repository.getFoodItems()

        assertEquals(2, items.size)
        assertEquals("Apple", items[0].name)
        assertEquals("Produce", items[0].category.name)
        assertEquals(0.99, items[0].price, 0.001)
        assertEquals("Meat", items[1].category.name)
    }

    @Test
    fun `falls back to Uncategorized when category uuid is unknown`() = runTest {
        coEvery { api.getFoodItems() } returns listOf(
            itemDto("item-1", "Mystery snack", 2.49, "cat-unknown"),
        )
        coEvery { api.getFoodCategories() } returns listOf(produceDto)

        val items = repository.getFoodItems()

        assertEquals("Uncategorized", items[0].category.name)
        assertEquals("cat-unknown", items[0].category.uuid)
    }

    @Test
    fun `getCategories maps dtos to domain models`() = runTest {
        coEvery { api.getFoodCategories() } returns listOf(produceDto, meatDto)

        val categories = repository.getCategories()

        assertEquals(listOf("Produce", "Meat"), categories.map { it.name })
        assertEquals(listOf("cat-produce", "cat-meat"), categories.map { it.uuid })
    }
}
