package com.ardondev.tiendita.domain.repository

import com.ardondev.tiendita.domain.model.Sale
import kotlinx.coroutines.flow.Flow

interface SaleRepository {

    suspend fun insert(sale: Sale): Long

    fun getAllByProductId(productId: Long, startDate: String, endDate: String): Flow<List<Sale>>

    fun getTotalOfSales(): Flow<Double?>

    fun getTotalOfSalesByProductId(productId: Long): Flow<Double?>

    suspend fun update(sale: Sale): Int

}