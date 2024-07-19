package com.ardondev.tiendita.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ardondev.tiendita.data.source.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Query("SELECT * FROM products")
    fun getAll(): Flow<List<ProductEntity>>

}