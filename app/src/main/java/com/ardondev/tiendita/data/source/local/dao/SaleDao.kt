package com.ardondev.tiendita.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardondev.tiendita.data.source.local.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    suspend fun insert(sale: SaleEntity): Long

    @Query("SELECT * FROM sales WHERE productId = :productId AND date BETWEEN :startDate AND :endDate ORDER BY date, time DESC")
    fun getAllByProductId(productId: Long, startDate: String, endDate: String): Flow<List<SaleEntity>>

    @Query("SELECT SUM(total) FROM sales")
    fun getTotalOfSales(): Flow<Double>

    @Query("SELECT SUM(total) FROM sales WHERE productId = :productId")
    fun getTotalOfSalesByProductId(productId: Long): Flow<Double?>

    @Update
    fun update(sale: SaleEntity): Int

}