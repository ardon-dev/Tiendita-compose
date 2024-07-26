package com.ardondev.tiendita.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String,
    val stock: Int,
    val price: Double,
    val totalSales: Double?
)
