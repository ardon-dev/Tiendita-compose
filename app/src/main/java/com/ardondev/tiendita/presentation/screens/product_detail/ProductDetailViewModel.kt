package com.ardondev.tiendita.presentation.screens.product_detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.domain.usecase.products.GetProductByIdUseCase
import com.ardondev.tiendita.domain.usecase.products.UpdateProductUseCase
import com.ardondev.tiendita.domain.usecase.sales.GetAllSalesByProductIdUseCase
import com.ardondev.tiendita.domain.usecase.sales.GetTotalOfSalesByProductIdUseCase
import com.ardondev.tiendita.domain.usecase.sales.InsertSaleUseCase
import com.ardondev.tiendita.presentation.screens.product_detail.product.ProductUiState
import com.ardondev.tiendita.presentation.screens.product_detail.sales.SalesUiState
import com.ardondev.tiendita.presentation.util.SingleEvent
import com.ardondev.tiendita.presentation.util.formatToUSD
import com.ardondev.tiendita.presentation.util.getCurrentDateTime
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
class ProductDetailViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    getProductByIdUseCase: GetProductByIdUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val insertSaleUseCase: InsertSaleUseCase,
    getAllSalesByProductIdUseCase: GetAllSalesByProductIdUseCase,
    private val getTotalOfSalesByProductIdUseCase: GetTotalOfSalesByProductIdUseCase,
) : ViewModel() {

    /** Loading **/

    var loading by mutableStateOf(false)
        private set

    fun setLoadingValue(value: Boolean) {
        loading = value
    }

    /** Product data **/

    private val productId: Long = savedStateHandle["product_id"] ?: 0L

    var product: Product? = null
        private set

    private fun setProduct(product: Product) {
        this.name = product.name
        this.stock = product.stock.toString()
        this.price = product.price.toString()
    }

    /** Get product **/

    val uiState: StateFlow<ProductUiState> = getProductByIdUseCase(productId)
        .map { product ->
            this.product = product
            setProduct(product)
            ProductUiState.Success(product) as ProductUiState
        }
        .catch { emit(ProductUiState.Error(it.message.orEmpty())) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ProductUiState.Loading
        )

    /** UI states **/

    var showBottomSheet by mutableStateOf(false)
        private set

    fun setShowBottomSheetValue(value: Boolean) {
        showBottomSheet = value
    }

    var tabPosition by mutableIntStateOf(0)
        private set

    fun setTabPositionValue(value: Int) {
        tabPosition = value

        //If selected tab is Sales, set add icon
        //If selected tab is Info, set edit icon
        fabIcon = if (tabPosition == 0) {
            Icons.Default.Add
        } else {
            //If is editable, set done icon
            if (editable) Icons.Default.Done else Icons.Default.Edit
        }
    }

    var fabIcon by mutableStateOf(Icons.Default.Add)
    var editable by mutableStateOf(false)
        private set

    fun setEditableValue(value: Boolean) {
        //Make information screen action
        if (tabPosition == 1) {
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
            return
        }

        //Make sales screen action
        if (tabPosition == 0) {
            showBottomSheet = true
            return
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

    /** Get sales **/

    val salesUiState: StateFlow<SalesUiState> = getAllSalesByProductIdUseCase(productId)
        .map { SalesUiState.Success(it) as SalesUiState }
        .catch { emit(SalesUiState.Error(it.message.orEmpty())) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SalesUiState.Loading
        )

    /** Get all sales **/

    var totalSales by mutableStateOf("0.00")
        private set

    private fun getTotalOfSales() {
        viewModelScope.launch {
            getTotalOfSalesByProductIdUseCase(productId).collectLatest { total ->
                total?.let {
                    totalSales = formatToUSD(total.toString())
                }
            }
        }
    }

    /** Insert sale **/

    private val _insertSaleResult = MutableLiveData<SingleEvent<Long?>>()
    val insertSaleResult: LiveData<SingleEvent<Long?>> = _insertSaleResult

    private val _insertSaleError = MutableLiveData<SingleEvent<Throwable?>>()
    val insertSaleError: LiveData<SingleEvent<Throwable?>> = _insertSaleError

    fun insertSale(price: Double, quantity: Int) {
        viewModelScope.launch {
            loading = true
            val sale = Sale(
                id = null,
                amount = price,
                quantity = quantity,
                total = (price * quantity),
                productId = productId,
                date = getCurrentDateTime()
            )
            val result = insertSaleUseCase(sale)
            if (result.isSuccess) {
                _insertSaleResult.value = SingleEvent(result.getOrNull())
                loading = false
                showBottomSheet = false
            } else {
                _insertSaleError.value = SingleEvent(result.exceptionOrNull())
                loading = false
                showBottomSheet = false
            }
        }
    }

    init {
        getTotalOfSales()
    }

}