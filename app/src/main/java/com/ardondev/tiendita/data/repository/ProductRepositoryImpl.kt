package com.ardondev.tiendita.data.repository

import com.ardondev.tiendita.data.source.local.dao.ProductDao
import com.ardondev.tiendita.data.toEntity
import com.ardondev.tiendita.data.toModel
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
) : ProductRepository {

    private val products: Flow<List<Product>> = productDao.getAll().map { products ->
        products.map { product ->
            product.toModel()
        }
    }

    override fun getAll(): Flow<List<Product>> = products

    override suspend fun insert(product: Product): Long {
        return productDao.insert(product.toEntity())
    }

    override fun getById(productId: Long): Flow<Product> {
        return productDao.getById(productId).map { it.toModel() }
    }

    override suspend fun update(product: Product): Int {
        return productDao.update(product.toEntity())
    }

}