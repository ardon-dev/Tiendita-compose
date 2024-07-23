package com.ardondev.tiendita.presentation.screens.product_detail.product

import com.ardondev.tiendita.domain.model.Product

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(val product: Product) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}