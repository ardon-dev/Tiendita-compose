package com.ardondev.tiendita.data.di

import com.ardondev.tiendita.data.repository.ProductRepositoryImpl
import com.ardondev.tiendita.data.repository.SaleRepositoryImpl
import com.ardondev.tiendita.data.source.local.dao.ProductDao
import com.ardondev.tiendita.data.source.local.dao.SaleDao
import com.ardondev.tiendita.domain.repository.ProductRepository
import com.ardondev.tiendita.domain.repository.SaleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideProductRepository(productDao: ProductDao): ProductRepository {
        return ProductRepositoryImpl(productDao)
    }

    @Provides
    fun provideSaleRepository(saleDao: SaleDao): SaleRepository {
        return SaleRepositoryImpl(saleDao)
    }

}