package com.ardondev.tiendita.domain.usecase.sales

import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSalesByProductIdUseCase @Inject constructor(
    private val saleRepository: SaleRepository,
) {

    operator fun invoke(productId: Long, startDate: String, endDate: String): Flow<List<Sale>> =
        saleRepository.getAllByProductId(productId, startDate, endDate)

}