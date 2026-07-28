package com.example.foodmart.ui.foodlist

import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.model.FoodItem

enum class SortOrder {
    NONE,
    PRICE_ASCENDING,
    PRICE_DESCENDING,
}

data class FoodListUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val items: List<FoodItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryUuids: Set<String> = emptySet(),
    val sortOrder: SortOrder = SortOrder.NONE,
) {

    val visibleItems: List<FoodItem>
        get() {
            val filtered = if (selectedCategoryUuids.isEmpty()) {
                items
            } else {
                items.filter { it.category.uuid in selectedCategoryUuids }
            }
            return when (sortOrder) {
                SortOrder.NONE -> filtered
                SortOrder.PRICE_ASCENDING -> filtered.sortedBy { it.price }
                SortOrder.PRICE_DESCENDING -> filtered.sortedByDescending { it.price }
            }
        }
}
