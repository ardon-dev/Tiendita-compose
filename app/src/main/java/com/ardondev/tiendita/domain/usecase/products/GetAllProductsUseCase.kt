package com.ardondev.tiendita.domain.usecase.products

import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator fun invoke(): Flow<List<Product>> = productRepository.getAll()

}