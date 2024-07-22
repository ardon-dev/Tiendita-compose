package com.ardondev.tiendita.presentation.screens.product_detail.sales

import com.ardondev.tiendita.domain.model.Sale

sealed class SalesUiState {
    object Loading : SalesUiState()
    data class Success(val sales: List<Sale>) : SalesUiState()
    data class Error(val message: String) : SalesUiState()
}