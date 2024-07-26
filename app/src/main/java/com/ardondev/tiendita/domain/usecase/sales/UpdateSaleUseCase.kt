package com.ardondev.tiendita.domain.usecase.sales

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.util.query
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.ProductRepository
import com.ardondev.tiendita.domain.repository.SaleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class UpdateSaleUseCase @Inject constructor(
    private val salesRepository: SaleRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(sale: Sale, oldQuantity: Int): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                //Update sale
                val result = salesRepository.update(sale)
                //Update product stock if product was upgraded
                val rowWasModified = result > 0
                if (rowWasModified) updateProductStock(sale, oldQuantity)
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
        sale: Sale,
        oldQuantity: Int
    ) {
        //Update product stock
        val newQuantity = sale.quantity
        if (newQuantity < oldQuantity) {
            productRepository.addStock(
                productId = sale.productId,
                quantity = (oldQuantity - newQuantity)
            )
        } else if (newQuantity > oldQuantity) {
            productRepository.decreaseStock(
                productId = sale.productId,
                quantity = (newQuantity - oldQuantity)
            )
        }
    }

}