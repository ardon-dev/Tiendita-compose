package com.ardondev.tiendita.domain.usecase.sales

import com.ardondev.tiendita.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalOfSalesUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {

    operator fun invoke(): Flow<Double> {
        return saleRepository.getTotalOfSales()
    }

}