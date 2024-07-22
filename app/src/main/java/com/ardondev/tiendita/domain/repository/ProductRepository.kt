package com.ardondev.tiendita.domain.repository

import com.ardondev.tiendita.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAll(): Flow<List<Product>>

    suspend fun insert(product: Product): Long

    fun getById(productId: Long): Flow<Product>

    suspend fun update(product: Product): Int

}