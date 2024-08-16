package com.ardondev.tiendita.domain.usecase.sales

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ardondev.tiendita.data.repository.ProductRepositoryImpl
import com.ardondev.tiendita.data.repository.SaleRepositoryImpl
import com.ardondev.tiendita.data.source.local.StoreDatabase
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.ProductRepository
import com.ardondev.tiendita.domain.repository.SaleRepository
import com.ardondev.tiendita.presentation.util.getCurrentDate
import com.ardondev.tiendita.presentation.util.getCurrentTime
import kotlinx.coroutines.test.runTest
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class SalesUseCasesTestRule : TestRule {

    val productId = 1L
    val saleId = 11L
    lateinit var testProduct: Product
    lateinit var testSale: Sale
    lateinit var database: StoreDatabase
    lateinit var saleRepository: SaleRepository
    lateinit var productRepository: ProductRepository

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                testProduct = createTestProduct(productId)
                testSale = createTestSale(
                    id = saleId,
                    productId = productId,
                    date = getCurrentDate(),
                    time = getCurrentTime()
                )

                database = Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    StoreDatabase::class.java
                ).build()
                productRepository = ProductRepositoryImpl(database.productDao())
                saleRepository = SaleRepositoryImpl(database.saleDao())
                try {
                    base?.evaluate()
                } finally {
                    database.close()
                }
            }
        }
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

    fun createTestSale(
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

}