package com.ardondev.tiendita.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.ardondev.tiendita.data.source.local.entity.SaleEntity

@Dao
interface SaleDao {

    @Insert
    suspend fun insert(sale: SaleEntity): Long

}