package com.ardondev.tiendita.domain.usecase.sales

import android.database.sqlite.SQLiteConstraintException
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.SaleRepository
import java.io.IOException
import javax.inject.Inject

class InsertSaleUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {

    suspend operator fun invoke(sale: Sale): Result<Long> {
        try {
            val result = saleRepository.insert(sale)
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