package com.example.foodmart.di

import com.example.foodmart.data.remote.FakePurchaseApi
import com.example.foodmart.data.remote.PurchaseApi
import com.example.foodmart.data.repository.CartRepositoryImpl
import com.example.foodmart.data.repository.FoodRepositoryImpl
import com.example.foodmart.data.repository.PurchaseRepositoryImpl
import com.example.foodmart.domain.repository.CartRepository
import com.example.foodmart.domain.repository.FoodRepository
import com.example.foodmart.domain.repository.PurchaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindPurchaseRepository(impl: PurchaseRepositoryImpl): PurchaseRepository

    @Binds
    @Singleton
    // Replace this when the API is actually implemented
    abstract fun bindPurchaseApi(impl: FakePurchaseApi): PurchaseApi
}
