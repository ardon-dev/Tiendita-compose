package com.ardondev.tiendita.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ardondev.tiendita.data.source.local.entity.SaleEntity
import com.ardondev.tiendita.domain.model.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    suspend fun insert(sale: SaleEntity): Long

    @Query("SELECT * FROM sales WHERE productId = :productId")
    fun getAllByProductId(productId: Long): Flow<List<SaleEntity>>

}