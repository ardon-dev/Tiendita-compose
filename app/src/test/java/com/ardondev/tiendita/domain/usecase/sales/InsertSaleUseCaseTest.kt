package com.ardondev.tiendita.domain.usecase.sales

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ardondev.tiendita.data.repository.ProductRepositoryImpl
import com.ardondev.tiendita.data.repository.SaleRepositoryImpl
import com.ardondev.tiendita.data.source.local.StoreDatabase
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.ProductRepository
import com.ardondev.tiendita.domain.repository.SaleRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InsertSaleUseCaseTest {

    private lateinit var database: StoreDatabase
    private lateinit var productRepository: ProductRepository
    private lateinit var saleRepository: SaleRepository
    private lateinit var insertSaleUseCaseTest: InsertSaleUseCase
    private val productId = 1L
    private val saleId = 11L
    private val stock = 4

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoreDatabase::class.java
        ).build()
        productRepository = ProductRepositoryImpl(database.productDao())

        //Insert test product with stock
        runTest { productRepository.insert(createTestProduct(id = productId, stock = stock)) }

        saleRepository = SaleRepositoryImpl(database.saleDao())
        insertSaleUseCaseTest = InsertSaleUseCase(saleRepository, productRepository)
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

    private fun createTestSale(
        id: Long?,
        amount: Double = 0.0,
        byUnity: Boolean = false,
        quantity: Int = 0,
        total: Double = 0.0,
        productId: Long = -1L,
        date: String = "",
        time: String = ""
    ) : Sale {
        return Sale(id, amount, byUnity, quantity, total, productId, date, time)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert sale should decrease its product stock and returns success`() = runTest {
        //Given
        val quantity = stock.minus(1)
        val sale = createTestSale(id = saleId, productId = productId, quantity = quantity)
        val expectedStock = stock - quantity

        //When
        productRepository.getById(productId).test {

            //Insert a sale, should decrease product stock
            val insertResult = insertSaleUseCaseTest(sale)

            //Then
            val msg = "Product stock: ${awaitItem().stock}"
            assertTrue(insertResult.isSuccess)
            assertEquals(msg, expectedStock, awaitItem().stock)

        }

    }

    @Test
    fun `insert sale with quantity above product stock should fail and returns illegal exception`() = runTest {
        //Given
        val quantity = stock.minus(1) //For quantity minor or equal than stock case (should not pass)
        //val quantity = stock.plus(1) //For quantity more than stock case
        val sale = createTestSale(id = saleId, productId = productId, quantity = quantity)

        //When
        val result = insertSaleUseCaseTest(sale)

        //Then
        val msg = "Result: $result"
        assertTrue(msg, result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

}