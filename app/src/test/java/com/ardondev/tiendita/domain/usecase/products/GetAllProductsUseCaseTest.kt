package com.ardondev.tiendita.domain.usecase.products

import androidx.compose.runtime.collectAsState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ardondev.tiendita.data.repository.ProductRepositoryImpl
import com.ardondev.tiendita.data.source.local.StoreDatabase
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetAllProductsUseCaseTest {

    private lateinit var database: StoreDatabase
    private lateinit var productRepository: ProductRepository
    private lateinit var getAllProductsUseCase: GetAllProductsUseCase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).build()
        productRepository = ProductRepositoryImpl(database.productDao())
        getAllProductsUseCase = GetAllProductsUseCase(productRepository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createTestProduct(
        id: Long? = null,
        name: String = "Test Product",
        stock: Int = 0,
        price: Double = 0.00,
        totalSales: Double = 0.0,
        resId: Int = 0,
    ): Product {
        return Product(
            id = id,
            name = name,
            stock = stock,
            price = price,
            totalSales = totalSales,
            resId = resId
        )
    }

    @Test
    fun `get all without product inserted returns empty list`() = runTest {

        //When
        getAllProductsUseCase.invoke().test {

            //Then
            assertTrue(awaitItem().isEmpty())

        }

    }

    @Test
    fun `get all with product inserted returns not empty list`() = runTest {

        //Given
        val product = createTestProduct(1L)

        //When
        getAllProductsUseCase.invoke().test {

            //Then
            assertTrue(awaitItem().isEmpty())
            productRepository.insert(product)
            assertTrue(awaitItem().isNotEmpty())
        }

    }

}