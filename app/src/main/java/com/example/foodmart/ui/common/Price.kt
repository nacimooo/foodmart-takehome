package com.example.foodmart.ui.common

import java.util.Locale

fun Double.toPriceLabel(): String = String.format(Locale.US, "$%.2f", this)
