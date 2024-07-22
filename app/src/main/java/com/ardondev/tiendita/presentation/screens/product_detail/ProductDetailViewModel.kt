package com.ardondev.tiendita.presentation.screens.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardondev.tiendita.domain.usecase.products.GetProductByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    getProductByIdUseCase: GetProductByIdUseCase,
) : ViewModel() {

    val productId: Long = savedStateHandle["product_id"] ?: 0L

    /** Get product **/

    val uiState: StateFlow<ProductDetailUiState> = getProductByIdUseCase(productId)
        .map { product ->
            ProductDetailUiState.Success(product) as ProductDetailUiState
        }
        .catch { e ->
            emit(ProductDetailUiState.Error(e.message.orEmpty()))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ProductDetailUiState.Loading
        )

}