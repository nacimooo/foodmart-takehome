package com.example.foodmart.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmart.domain.repository.CartRepository
import com.example.foodmart.domain.repository.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val purchaseRepository: PurchaseRepository,
) : ViewModel() {

    private data class PurchaseState(
        val isPurchasing: Boolean = false,
        val userMessage: String? = null,
    )

    private val purchaseState = MutableStateFlow(PurchaseState())

    val uiState: StateFlow<CartUiState> =
        combine(cartRepository.items, purchaseState) { items, purchase ->
            CartUiState(
                items = items,
                isPurchasing = purchase.isPurchasing,
                userMessage = purchase.userMessage,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartUiState())

    fun onRemoveItem(foodItemUuid: String) {
        cartRepository.remove(foodItemUuid)
    }

    fun onPurchase() {
        val items = uiState.value.items
        if (items.isEmpty() || purchaseState.value.isPurchasing) return

        purchaseState.update { it.copy(isPurchasing = true) }
        viewModelScope.launch {
            try {
                purchaseRepository.purchase(items)
                // Only clear the cart once the purchase went through.
                cartRepository.clear()
                purchaseState.update {
                    it.copy(isPurchasing = false, userMessage = "Order placed successfully!")
                }
            } catch (e: Exception) {
                purchaseState.update {
                    it.copy(isPurchasing = false, userMessage = "Purchase failed. Please try again.")
                }
            }
        }
    }

    fun onUserMessageShown() {
        purchaseState.update { it.copy(userMessage = null) }
    }
}
