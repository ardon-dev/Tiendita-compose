package com.ardondev.tiendita.domain.usecase.products

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ardondev.tiendita.data.repository.ProductRepositoryImpl
import com.ardondev.tiendita.data.source.local.StoreDatabase
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateProductUseCaseTest {

    private lateinit var database: StoreDatabase
    private lateinit var productRepository: ProductRepository
    private lateinit var updateProductUseCase: UpdateProductUseCase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).build()
        productRepository = ProductRepositoryImpl(database.productDao())
        updateProductUseCase = UpdateProductUseCase(productRepository)
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
    fun `update product with non existing id should return success and 0 rows affected`() = runTest {
        //Given
        val product = createTestProduct(1L)
        val expected = 0

        //When
        val result = updateProductUseCase.invoke(product)
        val rowsAffected = result.getOrNull()

        //Then
        assertTrue(result.isSuccess)
        assertEquals(expected, rowsAffected)
    }

    @Test
    fun `update existing product should return success and 1 row affected`() = runTest {
        //Given
        val productId = 1L
        val product = createTestProduct(productId)
        val expected = 1

        //When
        productRepository.insert(product)
        val result = updateProductUseCase.invoke(product.copy(price = 0.10))
        val rowsAffected = result.getOrNull()

        //Then
        assertTrue(result.isSuccess)
        val msg = "Rows affected: $rowsAffected"
        assertEquals(msg, expected, rowsAffected)
    }

}