package com.ardondev.tiendita.presentation.screens.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.usecase.products.GetAllProductsUseCase
import com.ardondev.tiendita.domain.usecase.products.InsertProductUseCase
import com.ardondev.tiendita.domain.usecase.sales.GetTotalOfSalesUseCase
import com.ardondev.tiendita.presentation.screens.products.ProductsUiState.Success
import com.ardondev.tiendita.presentation.util.SingleEvent
import com.ardondev.tiendita.presentation.util.formatToUSD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val insertProductUseCase: InsertProductUseCase,
    getAllProductsUseCase: GetAllProductsUseCase,
    private val getTotalOfSalesUseCase: GetTotalOfSalesUseCase,
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /** Insert product **/

    private val _insertProductResult = MutableLiveData<SingleEvent<Long?>>()
    val insertProductResult: LiveData<SingleEvent<Long?>> = _insertProductResult

    private val _insertProductError = MutableLiveData<SingleEvent<String?>>()
    val insertProductError: LiveData<SingleEvent<String?>> = _insertProductError

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            _loading.value = true
            val result = insertProductUseCase(product)
            if (result.isSuccess) {
                _insertProductResult.value = SingleEvent(result.getOrNull())
                _loading.value = false
            } else {
                _insertProductError.value = SingleEvent(result.exceptionOrNull()?.message)
                _loading.value = false
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

    /** Total of sales **/

    var totalSales by mutableStateOf("0.00")
        private set

    private fun getTotalSales() {
        viewModelScope.launch {
            getTotalOfSalesUseCase().collectLatest { total ->
                totalSales = formatToUSD(total.toString() ?: "0.00")
            }
        }
    }

    init {
        getTotalSales()
    }

}