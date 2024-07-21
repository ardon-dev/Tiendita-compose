package com.ardondev.tiendita.domain.di

import com.ardondev.tiendita.domain.repository.ProductRepository
import com.ardondev.tiendita.domain.usecase.products.GetAllProductsUseCase
import com.ardondev.tiendita.domain.usecase.products.GetProductByIdUseCase
import com.ardondev.tiendita.domain.usecase.products.InsertProductUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    /** Product use cases **/

    @Provides
    @Singleton
    fun provideInsertProductUseCase(productRepository: ProductRepository): InsertProductUseCase {
        return InsertProductUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllProductsUseCase(productRepository: ProductRepository): GetAllProductsUseCase {
        return GetAllProductsUseCase(productRepository)
    }

    @Provides
    @Singleton
    fun provideGetProductByIdUseCase(productRepository: ProductRepository): GetProductByIdUseCase {
        return GetProductByIdUseCase(productRepository)
    }

}