package com.example.foodmart.data.repository

import com.example.foodmart.foodItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartRepositoryImplTest {

    private val repository = CartRepositoryImpl()

    private val apple = foodItem(uuid = "item-1", name = "Apple")
    private val steak = foodItem(uuid = "item-2", name = "Steak")

    @Test
    fun `adding the same item twice increments its quantity`() = runTest {
        repository.add(apple)
        repository.add(apple)

        val items = repository.items.first()

        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
    }

    @Test
    fun `adding different items creates separate cart lines`() = runTest {
        repository.add(apple)
        repository.add(steak)

        val items = repository.items.first()

        assertEquals(2, items.size)
        assertTrue(items.all { it.quantity == 1 })
    }

    @Test
    fun `removing an item deletes its whole cart line`() = runTest {
        repository.add(apple)
        repository.add(apple)
        repository.add(steak)

        repository.remove(apple.uuid)

        val items = repository.items.first()
        assertEquals(listOf("Steak"), items.map { it.foodItem.name })
    }

    @Test
    fun `clear empties the cart`() = runTest {
        repository.add(apple)
        repository.add(steak)

        repository.clear()

        assertTrue(repository.items.first().isEmpty())
    }
}
