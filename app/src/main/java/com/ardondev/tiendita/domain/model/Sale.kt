package com.ardondev.tiendita.domain.model

data class Sale(
    val id: Long?,
    val amount: Double,
    val quantity: Int,
    val total: Double,
    val productId: Long
)
