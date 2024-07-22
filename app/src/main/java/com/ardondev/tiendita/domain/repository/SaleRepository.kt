package com.ardondev.tiendita.domain.repository

import com.ardondev.tiendita.domain.model.Sale

interface SaleRepository {

    suspend fun insert(sale: Sale): Long

}