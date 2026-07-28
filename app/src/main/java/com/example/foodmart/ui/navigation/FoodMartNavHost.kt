package com.example.foodmart.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodmart.ui.cart.CartScreen
import com.example.foodmart.ui.foodlist.FoodListScreen

object Routes {
    const val FOOD_LIST = "food_list"
    const val CART = "cart"
}

@Composable
fun FoodMartNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.FOOD_LIST) {
        composable(Routes.FOOD_LIST) {
            FoodListScreen(
                onCartClick = { navController.navigate(Routes.CART) },
            )
        }
        composable(Routes.CART) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
