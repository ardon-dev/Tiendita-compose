package com.ardondev.tiendita.domain.usecase.products

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ardondev.tiendita.data.repository.ProductRepositoryImpl
import com.ardondev.tiendita.data.source.local.StoreDatabase
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InsertProductUseCaseTest {

    private lateinit var database: StoreDatabase
    private lateinit var productRepository: ProductRepository
    private lateinit var insertProductUseCase: InsertProductUseCase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).build()
        productRepository = ProductRepositoryImpl(database.productDao())
        insertProductUseCase = InsertProductUseCase(productRepository)
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
    fun `insertProduct with existing id should return failed result with SQL Exception`() =
        runBlocking {
            //Given
            val firstProduct = createTestProduct(id = 1L)
            val secondProduct = createTestProduct(id = 1L)

            //When
            val firstProductId = insertProductUseCase.invoke(firstProduct).getOrNull()
            Log.d("InsertProductUseCaseTest", "First product id: $firstProductId")
            val result = insertProductUseCase.invoke(secondProduct)

            //Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is SQLiteConstraintException)
        }

    @Test
    fun `insertProduct with another id should return success result`() =
        runBlocking {
            //Given
            val firstProduct = createTestProduct(id = 1L)
            val secondProduct = createTestProduct(id = 2L)
            val expected = 2L

            //When
            val firstProductId = insertProductUseCase.invoke(firstProduct).getOrNull()
            Log.d("InsertProductUseCaseTest", "First product id: $firstProductId")
            val result = insertProductUseCase.invoke(secondProduct)

            //Then
            assertTrue(result.isSuccess)
            assertEquals(result.getOrNull(), expected)
        }

}