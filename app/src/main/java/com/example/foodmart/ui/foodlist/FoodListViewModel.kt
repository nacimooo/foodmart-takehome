package com.example.foodmart.ui.foodlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmart.domain.model.FoodItem
import com.example.foodmart.domain.repository.CartRepository
import com.example.foodmart.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FoodListViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodListUiState())
    val uiState: StateFlow<FoodListUiState> = _uiState.asStateFlow()


    val cartItemCount: StateFlow<Int> = cartRepository.items
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                coroutineScope {
                    val itemsDeferred = async { foodRepository.getFoodItems() }
                    val categoriesDeferred = async { foodRepository.getCategories() }
                    val items = itemsDeferred.await()
                    val categories = categoriesDeferred.await()
                    _uiState.update {
                        it.copy(isLoading = false, items = items, categories = categories)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Couldn't load food items. Check your connection and try again.",
                    )
                }
            }
        }
    }

    fun onSortOrderSelected(sortOrder: SortOrder) {
        _uiState.update {

            // Tapping the active sort clears it back to the default order.
            val newOrder = if (it.sortOrder == sortOrder) SortOrder.NONE else sortOrder
            it.copy(sortOrder = newOrder)
        }
    }

    fun onCategoryToggled(categoryUuid: String) {
        _uiState.update {
            val selected = if (categoryUuid in it.selectedCategoryUuids) {
                it.selectedCategoryUuids - categoryUuid
            } else {
                it.selectedCategoryUuids + categoryUuid
            }
            it.copy(selectedCategoryUuids = selected)
        }
    }

    fun onAddToCart(item: FoodItem) {
        cartRepository.add(item)
    }
}
