package com.ardondev.tiendita.presentation.screens.product_detail

import com.ardondev.tiendita.domain.model.Product

sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}