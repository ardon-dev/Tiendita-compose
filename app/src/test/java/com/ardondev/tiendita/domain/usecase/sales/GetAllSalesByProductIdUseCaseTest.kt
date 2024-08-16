package com.ardondev.tiendita.domain.usecase.sales

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetAllSalesByProductIdUseCaseTest {

    @get:Rule
    val useCaseRule = SalesUseCasesTestRule()

    private lateinit var getAllSalesByProductIdUseCase: GetAllSalesByProductIdUseCase

    @Before
    fun setUp() {
        getAllSalesByProductIdUseCase = GetAllSalesByProductIdUseCase(useCaseRule.saleRepository)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun `get having sales within start and end date should return success and sales within range`() =
        runTest {
            //Given
            useCaseRule.productRepository.insert(useCaseRule.testProduct)

            val sale1 = useCaseRule.createTestSale(
                id = 1L,
                productId = useCaseRule.productId,
                date = "2024-08-15"
            )
            val sale2 = useCaseRule.createTestSale(
                id = 2L,
                productId = useCaseRule.productId,
                date = "2024-06-15"
            )
            val sale3 = useCaseRule.createTestSale(
                id = 3L,
                productId = useCaseRule.productId,
                date = "2024-10-15"
            )
            val sale4 = useCaseRule.createTestSale(
                id = 4L,
                productId = useCaseRule.productId,
                date = "2023-10-15"
            )
            val sales = listOf(sale1, sale2, sale3, sale4)
            val salesIn2024 = listOf(sale1, sale2, sale3).sortedBy { it.date }

            //When
            getAllSalesByProductIdUseCase(
                useCaseRule.productId,
                "2024-01-01",
                "2024-12-31"
            ).test { //All 2024 sales

                //Then
                assertTrue(awaitItem().isEmpty())

                //Insert all sales
                sales.forEach { useCaseRule.saleRepository.insert(it) }

                //Check not empty list
                assertTrue(awaitItem().isNotEmpty())

                //Check if returned only 2024 sales
                assertEquals(salesIn2024, awaitItem().sortedBy { it.date })

            }

        }

    @Test
    fun `get WITHOUT having sales within start and end date should return success and empty list`() =
        runTest {
            //Given
            useCaseRule.productRepository.insert(useCaseRule.testProduct)

            val sale1 = useCaseRule.createTestSale(
                id = 1L,
                productId = useCaseRule.productId,
                date = "2024-09-01"
            )
            val sale2 = useCaseRule.createTestSale(
                id = 2L,
                productId = useCaseRule.productId,
                date = "2024-09-02"
            )
            val septemberSales = listOf(sale1, sale2)

            //When
            getAllSalesByProductIdUseCase(
                useCaseRule.productId,
                "2024-08-01",
                "2024-08-31"
            ).test { //All 2024 August sales

                //Then
                assertTrue(awaitItem().isEmpty())

                //Insert all sales
                septemberSales.forEach { useCaseRule.saleRepository.insert(it) }

                //Check empty list
                assertTrue(awaitItem().isEmpty())

            }

        }

}