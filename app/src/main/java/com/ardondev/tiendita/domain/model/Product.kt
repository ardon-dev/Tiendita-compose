package com.ardondev.tiendita.domain.model

data class Product(
    val id: Long?,
    val name: String,
    val stock: Int,
    val price: Double,
    val totalSales: Double?,
    val resId: Int
)
