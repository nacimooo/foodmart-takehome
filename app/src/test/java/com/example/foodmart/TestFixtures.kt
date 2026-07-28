package com.example.foodmart

import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.model.FoodItem

val produceCategory = Category(uuid = "cat-produce", name = "Produce")
val meatCategory = Category(uuid = "cat-meat", name = "Meat")

fun foodItem(
    uuid: String = "item-1",
    name: String = "Apple",
    price: Double = 0.99,
    category: Category = produceCategory,
    imageUrl: String = "https://example.com/$uuid.png",
) = FoodItem(uuid = uuid, name = name, price = price, category = category, imageUrl = imageUrl)

fun cartItem(
    item: FoodItem = foodItem(),
    quantity: Int = 1,
) = CartItem(foodItem = item, quantity = quantity)
