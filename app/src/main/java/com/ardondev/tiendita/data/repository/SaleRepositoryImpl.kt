package com.ardondev.tiendita.data.repository

import com.ardondev.tiendita.data.source.local.dao.SaleDao
import com.ardondev.tiendita.data.toEntity
import com.ardondev.tiendita.data.toModel
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val saleDao: SaleDao
) : SaleRepository {

    override suspend fun insert(sale: Sale): Long {
        return saleDao.insert(sale.toEntity())
    }

    override fun getAllByProductId(productId: Long): Flow<List<Sale>> {
        return saleDao.getAllByProductId(productId).map { sales ->
            sales.map { sale ->
                sale.toModel()
            }
        }
    }

    override fun getTotalOfSales(): Flow<Double> {
        return saleDao.getTotalOfSales()
    }

    override fun getTotalOfSalesByProductId(productId: Long): Flow<Double?> {
        return saleDao.getTotalOfSalesByProductId(productId)
    }

}