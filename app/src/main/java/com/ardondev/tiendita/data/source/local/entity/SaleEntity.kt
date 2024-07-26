package com.ardondev.tiendita.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("productId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val amount: Double,
    val quantity: Int,
    val total: Double,
    val productId: Long,
    val date: String,
    val time: String
)
