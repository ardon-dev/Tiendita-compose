package com.ardondev.tiendita.presentation.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.usecase.products.GetAllProductsUseCase
import com.ardondev.tiendita.domain.usecase.products.InsertProductUseCase
import com.ardondev.tiendita.presentation.products.ProductsUiState.Success
import com.ardondev.tiendita.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val insertProductUseCase: InsertProductUseCase,
    getAllProductsUseCase: GetAllProductsUseCase,
) : ViewModel() {

    /** Insert product **/

    private val _insertProductResult = SingleLiveEvent<Long?>()
    val insertProductResult: LiveData<Long?> = _insertProductResult

    private val _insertProductError = SingleLiveEvent<Throwable?>()
    val insertProductError: LiveData<Throwable?> = _insertProductError

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            val result = insertProductUseCase(product)
            if (result.isSuccess) {
                _insertProductResult.value = result.getOrNull()
            } else {
                _insertProductError.value = result.exceptionOrNull()
            }
        }
    }

    /** Get all products **/

    val uiState: StateFlow<ProductsUiState> = getAllProductsUseCase()
        .map { products ->
            Success(products) as ProductsUiState
        }
        .catch { e ->
            emit(ProductsUiState.Error(e.message.orEmpty()))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ProductsUiState.Loading
        )

}