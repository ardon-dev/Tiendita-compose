package com.ardondev.tiendita.data.repository

import com.ardondev.tiendita.data.source.local.dao.SaleDao
import com.ardondev.tiendita.data.toEntity
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.SaleRepository
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val saleDao: SaleDao
) : SaleRepository {

    override suspend fun insert(sale: Sale): Long {
        return saleDao.insert(sale.toEntity())
    }

}