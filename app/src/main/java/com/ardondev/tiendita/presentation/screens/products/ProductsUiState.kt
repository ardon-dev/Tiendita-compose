package com.ardondev.tiendita.presentation.screens.products

import com.ardondev.tiendita.domain.model.Product

sealed class ProductsUiState {
    object Loading: ProductsUiState()
    data class Success(val products: List<Product>): ProductsUiState()
    data class Error(val message: String): ProductsUiState()
}