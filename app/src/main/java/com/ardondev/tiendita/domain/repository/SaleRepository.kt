package com.ardondev.tiendita.domain.repository

import com.ardondev.tiendita.domain.model.Sale
import kotlinx.coroutines.flow.Flow

interface SaleRepository {

    suspend fun insert(sale: Sale): Long

    fun getAllByProductId(productId: Long): Flow<List<Sale>>

}