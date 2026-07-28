package com.example.foodmart.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verifies the DTOs parse the JSON shapes the real endpoints return
 * (samples taken from the live API).
 */
class FoodDtoSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parses a food item from api json`() {
        val payload = """
            {
              "uuid": "a1f7b3e5-4c1d-42e9-8f2a-8cbb8b1f6f01",
              "name": "Bananas",
              "price": 1.49,
              "category_uuid": "b1f6d8a5-0e29-4d70-8d4f-1f8c1d7a5b12",
              "image_url": "https://7shifts.github.io/mobile-takehome/images/bananas.png"
            }
        """.trimIndent()

        val dto = json.decodeFromString<FoodItemDto>(payload)

        assertEquals("a1f7b3e5-4c1d-42e9-8f2a-8cbb8b1f6f01", dto.uuid)
        assertEquals("Bananas", dto.name)
        assertEquals(1.49, dto.price, 0.001)
        assertEquals("b1f6d8a5-0e29-4d70-8d4f-1f8c1d7a5b12", dto.categoryUuid)
        assertEquals("https://7shifts.github.io/mobile-takehome/images/bananas.png", dto.imageUrl)
    }

    @Test
    fun `parses a list of categories from api json`() {
        val payload = """
            [
              { "uuid": "b1f6d8a5-0e29-4d70-8d4f-1f8c1d7a5b12", "name": "Produce" },
              { "uuid": "f3a6c4e2-1d4c-4a3c-8d3d-6b8c15f0e2b9", "name": "Meat" }
            ]
        """.trimIndent()

        val categories = json.decodeFromString<List<FoodCategoryDto>>(payload)

        assertEquals(2, categories.size)
        assertEquals("Produce", categories[0].name)
        assertEquals("Meat", categories[1].name)
    }

    @Test
    fun `ignores unknown fields so backend can add new ones`() {
        val payload = """
            { "uuid": "cat-1", "name": "Produce", "sort_order": 3 }
        """.trimIndent()

        val dto = json.decodeFromString<FoodCategoryDto>(payload)

        assertEquals("cat-1", dto.uuid)
        assertEquals("Produce", dto.name)
    }
}
