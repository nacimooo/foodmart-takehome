package com.example.foodmart.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.foodmart.domain.model.CartItem
import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.model.FoodItem
import com.example.foodmart.ui.common.toPriceLabel
import com.example.foodmart.ui.theme.FoodMartTheme

@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CartContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRemoveItem = viewModel::onRemoveItem,
        onPurchase = viewModel::onPurchase,
        onUserMessageShown = viewModel::onUserMessageShown,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartContent(
    uiState: CartUiState,
    onBackClick: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onPurchase: () -> Unit,
    onUserMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            onUserMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                PurchaseBar(
                    totalPrice = uiState.totalPrice,
                    isPurchasing = uiState.isPurchasing,
                    onPurchase = onPurchase,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (uiState.items.isEmpty()) {
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.items, key = { it.foodItem.uuid }) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onRemove = { onRemoveItem(cartItem.foodItem.uuid) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onRemove: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = cartItem.foodItem.imageUrl,
                contentDescription = cartItem.foodItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.foodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${cartItem.foodItem.price.toPriceLabel()} x ${cartItem.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = cartItem.lineTotal.toPriceLabel(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove ${cartItem.foodItem.name} from cart",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun PurchaseBar(
    totalPrice: Double,
    isPurchasing: Boolean,
    onPurchase: () -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = totalPrice.toPriceLabel(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            Button(
                onClick = onPurchase,
                enabled = !isPurchasing,
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Purchase")
                }
            }
        }
    }
}

// region Previews

private val previewCartItems = listOf(
    CartItem(
        foodItem = FoodItem("item-1", "Bananas", 1.49, Category("cat-produce", "Produce"), ""),
        quantity = 2,
    ),
    CartItem(
        foodItem = FoodItem("item-2", "Apple", 0.99, Category("cat-produce", "Produce"), ""),
        quantity = 1,
    ),
)

@Preview(showBackground = true)
@Composable
private fun CartPreview() {
    FoodMartTheme {
        CartContent(
            uiState = CartUiState(items = previewCartItems),
            onBackClick = {},
            onRemoveItem = {},
            onPurchase = {},
            onUserMessageShown = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartPurchasingPreview() {
    FoodMartTheme {
        CartContent(
            uiState = CartUiState(items = previewCartItems, isPurchasing = true),
            onBackClick = {},
            onRemoveItem = {},
            onPurchase = {},
            onUserMessageShown = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartEmptyPreview() {
    FoodMartTheme {
        CartContent(
            uiState = CartUiState(),
            onBackClick = {},
            onRemoveItem = {},
            onPurchase = {},
            onUserMessageShown = {},
        )
    }
}

// endregion
