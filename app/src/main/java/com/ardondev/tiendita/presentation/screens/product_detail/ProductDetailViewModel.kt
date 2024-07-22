package com.ardondev.tiendita.presentation.screens.product_detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.usecase.products.GetProductByIdUseCase
import com.ardondev.tiendita.domain.usecase.products.UpdateProductUseCase
import com.ardondev.tiendita.presentation.util.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    getProductByIdUseCase: GetProductByIdUseCase,
    private val updateProductUseCase: UpdateProductUseCase
) : ViewModel() {

    /** Loading **/

    var loading by mutableStateOf(false)
        private set

    fun setLoadingValue(value: Boolean) {
        loading = value
    }

    /** Product data **/

    private val productId: Long = savedStateHandle["product_id"] ?: 0L
    private var product: Product? = null

    private fun setProduct(product: Product) {
        this.name = product.name
        this.stock = product.stock.toString()
        this.price = product.price.toString()
    }

    /** Get product **/

    val uiState: StateFlow<ProductDetailUiState> = getProductByIdUseCase(productId)
        .map { product ->
            this.product = product
            setProduct(product)
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

    /** UI states **/

    var tabPosition by mutableStateOf(0)
        private set

    fun setTabPositionValue(value: Int) {
        tabPosition = value
    }

    var fabIcon by mutableStateOf(Icons.Default.Edit)
    var editable by mutableStateOf(false)
        private set

    fun setEditableValue(value: Boolean) {
        editable = value
        if (editable) {
            fabIcon = Icons.Default.Done
        } else {
            //If content is the same, do not update
            val currentProduct = Product(productId, name, stock.toInt(), price.toDouble())
            if (product == currentProduct) {
                fabIcon = Icons.Default.Edit
                return
            }
            //Otherwise, update the product
            updateProduct(currentProduct)
        }
    }

    var name by mutableStateOf("")
        private set

    fun setNameValue(value: String) {
        name = value
    }

    var stock by mutableStateOf("")
        private set

    fun setStockValue(value: String) {
        stock = value
    }

    var price by mutableStateOf("")
        private set

    fun setPriceValue(value: String) {
        price = value
    }

    /** Update product **/

    private val _updateProductResult = MutableLiveData<SingleEvent<Int?>>()
    val updateProductResult: LiveData<SingleEvent<Int?>> = _updateProductResult

    private val _updateProductError = MutableLiveData<SingleEvent<Throwable?>>()
    val updateProductError: LiveData<SingleEvent<Throwable?>> = _updateProductError

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            loading = true
            try {
                val result = updateProductUseCase(product)
                if (result.isSuccess) {
                    _updateProductResult.value = SingleEvent(result.getOrNull())
                    loading = false
                    fabIcon = Icons.Default.Edit
                } else {
                    _updateProductError.value = SingleEvent(result.exceptionOrNull())
                    loading = false
                }
            } catch (e: Exception) {
                _updateProductError.value = SingleEvent(e)
                loading = false
            }
        }
    }

}