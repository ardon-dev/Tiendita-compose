package com.ardondev.tiendita.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ardondev.tiendita.data.source.local.dao.ProductDao
import com.ardondev.tiendita.data.source.local.dao.SaleDao
import com.ardondev.tiendita.data.source.local.entity.ProductEntity
import com.ardondev.tiendita.data.source.local.entity.SaleEntity

@Database(
    entities = [ProductEntity::class, SaleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class StoreDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao

}