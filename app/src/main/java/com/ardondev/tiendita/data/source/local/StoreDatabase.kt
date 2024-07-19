package com.ardondev.tiendita.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ardondev.tiendita.data.source.local.dao.ProductDao
import com.ardondev.tiendita.data.source.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoreDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDao

}