package com.ardondev.tiendita.domain.usecase.products

import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import java.io.IOException
import javax.inject.Inject

class InsertProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {

    suspend operator fun invoke(product: Product): Result<Long> {
        try {
            val result = productRepository.insert(product)
            return Result.success(result)
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

}