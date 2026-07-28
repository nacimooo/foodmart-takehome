package com.example.foodmart.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmart.domain.usecase.ObserveCartUseCase
import com.example.foodmart.domain.usecase.PurchaseCartUseCase
import com.example.foodmart.domain.usecase.RemoveFromCartUseCase
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
    observeCart: ObserveCartUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val purchaseCart: PurchaseCartUseCase,
) : ViewModel() {

    private data class PurchaseState(
        val isPurchasing: Boolean = false,
        val userMessage: String? = null,
    )

    private val purchaseState = MutableStateFlow(PurchaseState())

    val uiState: StateFlow<CartUiState> =
        combine(observeCart(), purchaseState) { items, purchase ->
            CartUiState(
                items = items,
                isPurchasing = purchase.isPurchasing,
                userMessage = purchase.userMessage,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartUiState())

    fun onRemoveItem(foodItemUuid: String) {
        removeFromCart(foodItemUuid)
    }

    fun onPurchase() {
        val items = uiState.value.items
        if (items.isEmpty() || purchaseState.value.isPurchasing) return

        purchaseState.update { it.copy(isPurchasing = true) }
        viewModelScope.launch {
            try {
                purchaseCart(items)
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
