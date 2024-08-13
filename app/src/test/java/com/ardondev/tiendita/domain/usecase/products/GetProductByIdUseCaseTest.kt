package com.ardondev.tiendita.domain.usecase.products

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ardondev.tiendita.data.repository.ProductRepositoryImpl
import com.ardondev.tiendita.data.source.local.StoreDatabase
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.function.ThrowingRunnable
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetProductByIdUseCaseTest {

    private lateinit var database: StoreDatabase
    private lateinit var productRepository: ProductRepository
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).build()
        productRepository = ProductRepositoryImpl(database.productDao())
        getProductByIdUseCase = GetProductByIdUseCase(productRepository)
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
    fun `get product by id not found returns exception`() = runTest {
        val fakeProductId = 1L
        getProductByIdUseCase.invoke(fakeProductId).test {
            val throwable = awaitError()
            assertTrue(throwable is Exception)
        }
    }

    @Test
    fun `get product by id found returns product`() = runTest {

        //Given
        val productId = 1L
        val product = createTestProduct(productId)

        //When
        productRepository.insert(product)
        getProductByIdUseCase.invoke(productId).test {

            //Then
            val result = awaitItem()
            assertNotNull(result)
            assertEquals(result, product)

        }
    }

    @Test
    fun `get product by id value updates should return same product with updated value`() = runTest {

        //Given
        val productId = 1L
        val product = createTestProduct(productId)
        val price = 0.01

        //When
        productRepository.insert(product)
        getProductByIdUseCase.invoke(productId).test {

            //Then - Check if product is found
            val result = awaitItem()
            assertNotNull(result)
            assertEquals(result, product)

            //Then - Update this product's price
            productRepository.update(product.copy(price = price))

            //Then - Check if value was updated
            val newResult = awaitItem()
            assertEquals(price, newResult.price, 0.0) //UPDATED VALUE
            assertEquals(product.id, newResult.id)
            assertEquals(product.totalSales, newResult.totalSales)
            assertEquals(product.name, newResult.name)
            assertEquals(product.stock, newResult.stock)
            assertEquals(product.resId, newResult.resId)

        }
    }

}