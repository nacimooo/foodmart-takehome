package com.example.foodmart.ui.foodlist

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.foodmart.domain.model.Category
import com.example.foodmart.domain.model.FoodItem
import com.example.foodmart.ui.common.toPriceLabel
import com.example.foodmart.ui.theme.FoodMartTheme

@Composable
fun FoodListScreen(
    onCartClick: () -> Unit,
    viewModel: FoodListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()

    FoodListContent(
        uiState = uiState,
        cartItemCount = cartItemCount,
        onCartClick = onCartClick,
        onSortOrderSelected = viewModel::onSortOrderSelected,
        onCategoryToggled = viewModel::onCategoryToggled,
        onAddToCart = viewModel::onAddToCart,
        onRetry = viewModel::load,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodListContent(
    uiState: FoodListUiState,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    onSortOrderSelected: (SortOrder) -> Unit,
    onCategoryToggled: (String) -> Unit,
    onAddToCart: (FoodItem) -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Mart") },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge { Text(cartItemCount.toString()) }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Shopping cart",
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.errorMessage != null -> {
                    ErrorState(
                        message = uiState.errorMessage,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        FilterControls(
                            uiState = uiState,
                            onSortOrderSelected = onSortOrderSelected,
                            onCategoryToggled = onCategoryToggled,
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(uiState.visibleItems, key = { it.uuid }) { item ->
                                FoodItemCard(item = item, onAddToCart = { onAddToCart(item) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterControls(
    uiState: FoodListUiState,
    onSortOrderSelected: (SortOrder) -> Unit,
    onCategoryToggled: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = uiState.sortOrder == SortOrder.PRICE_ASCENDING,
                onClick = { onSortOrderSelected(SortOrder.PRICE_ASCENDING) },
                label = { Text("Price: Low to High") },
            )
            FilterChip(
                selected = uiState.sortOrder == SortOrder.PRICE_DESCENDING,
                onClick = { onSortOrderSelected(SortOrder.PRICE_DESCENDING) },
                label = { Text("Price: High to Low") },
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.categories, key = { it.uuid }) { category ->
                FilterChip(
                    selected = category.uuid in uiState.selectedCategoryUuids,
                    onClick = { onCategoryToggled(category.uuid) },
                    label = { Text(category.name) },
                )
            }
        }
    }
}

@Composable
private fun FoodItemCard(
    item: FoodItem,
    onAddToCart: () -> Unit,
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
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = item.category.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = item.price.toPriceLabel(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            FilledTonalIconButton(onClick = onAddToCart) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add ${item.name} to cart",
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// region Previews

private val previewProduce = Category(uuid = "cat-produce", name = "Produce")
private val previewMeat = Category(uuid = "cat-meat", name = "Meat")

private val previewItems = listOf(
    FoodItem("item-1", "Bananas", 1.49, previewProduce, ""),
    FoodItem("item-2", "Apple", 0.99, previewProduce, ""),
    FoodItem("item-3", "Steak", 16.49, previewMeat, ""),
)

@Preview(showBackground = true)
@Composable
private fun FoodListPreview() {
    FoodMartTheme {
        FoodListContent(
            uiState = FoodListUiState(
                isLoading = false,
                items = previewItems,
                categories = listOf(previewProduce, previewMeat),
                selectedCategoryUuids = setOf(previewProduce.uuid),
            ),
            cartItemCount = 3,
            onCartClick = {},
            onSortOrderSelected = {},
            onCategoryToggled = {},
            onAddToCart = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodListLoadingPreview() {
    FoodMartTheme {
        FoodListContent(
            uiState = FoodListUiState(isLoading = true),
            cartItemCount = 0,
            onCartClick = {},
            onSortOrderSelected = {},
            onCategoryToggled = {},
            onAddToCart = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodListErrorPreview() {
    FoodMartTheme {
        FoodListContent(
            uiState = FoodListUiState(
                isLoading = false,
                errorMessage = "Couldn't load food items. Check your connection and try again.",
            ),
            cartItemCount = 0,
            onCartClick = {},
            onSortOrderSelected = {},
            onCategoryToggled = {},
            onAddToCart = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodItemCardPreview() {
    FoodMartTheme {
        FoodItemCard(item = previewItems.first(), onAddToCart = {})
    }
}

// endregion
