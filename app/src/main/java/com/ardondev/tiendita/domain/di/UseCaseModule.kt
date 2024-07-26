package com.ardondev.tiendita.domain.di

import com.ardondev.tiendita.domain.repository.ProductRepository
import com.ardondev.tiendita.domain.repository.SaleRepository
import com.ardondev.tiendita.domain.usecase.products.GetAllProductsUseCase
import com.ardondev.tiendita.domain.usecase.products.GetProductByIdUseCase
import com.ardondev.tiendita.domain.usecase.products.InsertProductUseCase
import com.ardondev.tiendita.domain.usecase.products.UpdateProductUseCase
import com.ardondev.tiendita.domain.usecase.sales.DeleteSaleUseCase
import com.ardondev.tiendita.domain.usecase.sales.GetAllSalesByProductIdUseCase
import com.ardondev.tiendita.domain.usecase.sales.GetTotalOfSalesByProductIdUseCase
import com.ardondev.tiendita.domain.usecase.sales.GetTotalOfSalesUseCase
import com.ardondev.tiendita.domain.usecase.sales.InsertSaleUseCase
import com.ardondev.tiendita.domain.usecase.sales.UpdateSaleUseCase
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

    @Provides
    @Singleton
    fun provideUpdateProductUseCase(productRepository: ProductRepository): UpdateProductUseCase {
        return UpdateProductUseCase(productRepository)
    }

    /** Sale use cases **/

    @Provides
    @Singleton
    fun provideInsertSaleUseCase(
        saleRepository: SaleRepository,
        productRepository: ProductRepository,
    ): InsertSaleUseCase {
        return InsertSaleUseCase(saleRepository, productRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllSalesByProductIdUseCase(saleRepository: SaleRepository): GetAllSalesByProductIdUseCase {
        return GetAllSalesByProductIdUseCase(saleRepository)
    }

    @Provides
    fun provideGetTotalOfSalesUseCase(saleRepository: SaleRepository): GetTotalOfSalesUseCase {
        return GetTotalOfSalesUseCase(saleRepository)
    }

    @Provides
    fun provideGetTotalOfSalesByProductIdUseCase(saleRepository: SaleRepository): GetTotalOfSalesByProductIdUseCase {
        return GetTotalOfSalesByProductIdUseCase(saleRepository)
    }

    @Provides
    fun provideUpdateSaleUseCase(
        salesRepository: SaleRepository,
        productRepository: ProductRepository,
    ): UpdateSaleUseCase {
        return UpdateSaleUseCase(salesRepository, productRepository)
    }

    @Provides
    fun provideDeleteSaleUseCase(
        saleRepository: SaleRepository,
        productRepository: ProductRepository,
    ): DeleteSaleUseCase {
        return DeleteSaleUseCase(saleRepository, productRepository)
    }

}