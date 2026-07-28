package com.example.foodmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foodmart.ui.navigation.FoodMartNavHost
import com.example.foodmart.ui.theme.FoodMartTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // little aesthetic change
        enableEdgeToEdge()

        setContent {
            FoodMartTheme {
                FoodMartNavHost()
            }
        }
    }
}
