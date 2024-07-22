package com.ardondev.tiendita.domain.usecase.products

import android.database.sqlite.SQLiteConstraintException
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.repository.ProductRepository
import java.io.IOException
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(product: Product): Result<Int> {
        try {
            val result = productRepository.update(product)
            return Result.success(result)
        } catch (e: IOException) {
            return Result.failure(e)
        } catch (e: SQLiteConstraintException) {
            return Result.failure(e)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

}