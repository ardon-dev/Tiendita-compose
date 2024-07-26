package com.ardondev.tiendita.domain.usecase.sales

import android.database.sqlite.SQLiteConstraintException
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.ProductRepository
import com.ardondev.tiendita.domain.repository.SaleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class DeleteSaleUseCase @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(sale: Sale): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                //Delete sale
                val result = saleRepository.delete(sale)
                //Update product stock if product was upgraded
                val rowWasModified = result > 0
                if (rowWasModified) updateProductStock(sale)
                //Return
                Result.success(result)
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SQLiteConstraintException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateProductStock(
        sale: Sale
    ) {
        productRepository.addStock(
            productId = sale.productId,
            quantity = sale.quantity
        )
    }

}