package com.ardondev.tiendita.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardondev.tiendita.data.source.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Query("""SELECT 
            products.*, 
            IFNULL(SUM(sales.total), 0) AS totalSales
        FROM products
        LEFT JOIN sales ON products.id = sales.productId
        GROUP BY products.id""")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getById(productId: Long): Flow<ProductEntity>

    @Update
    suspend fun update(product: ProductEntity): Int

    @Query("UPDATE products SET stock = (stock - :quantity) WHERE id = :productId")
    suspend fun updateStock(productId: Long, quantity: Int): Int

}