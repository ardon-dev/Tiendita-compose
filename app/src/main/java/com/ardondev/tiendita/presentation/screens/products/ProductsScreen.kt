package com.ardondev.tiendita.presentation.screens.products

import android.app.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ardondev.tiendita.R
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.LoadingView
import com.ardondev.tiendita.presentation.util.SingleEvent
import kotlinx.coroutines.launch

@Preview
@Composable
fun HomeScreenPreview() {
    val products = mutableListOf<Product>().apply {
        repeat(5) {
            add(Product(it.toLong(), "a", 1, 0.10))
        }
    }

    Column {
        ProductList(innerPadding = PaddingValues(16.dp), products = products)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val loadingState by viewModel.loading.observeAsState()

    val uiState by produceState<ProductsUiState>(
        initialValue = ProductsUiState.Loading,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { value = it }
        }
    }

    /** Events **/

    viewModel.insertProductResult.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { id ->
            id?.let {
                scope.launch {
                    snackBarHostState.showSnackbar("Inserted: $id")
                }
                showBottomSheet = false
            }
        })

    viewModel.insertProductError.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { error ->
            error?.let {
                scope.launch {
                    snackBarHostState.showSnackbar(error)
                }
                showBottomSheet = false
            }
        })

    /** Products **/

    Scaffold(
        //ADD FAB
        floatingActionButton = {
            ProductsFAB(
                onAdd = { showBottomSheet = true }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        /** PRODUCTS **/

        when (uiState) {
            ProductsUiState.Loading -> {
                LoadingView()
            }

            is ProductsUiState.Error -> {
                val message = (uiState as ProductsUiState.Error).message
                ErrorView(message)
            }

            is ProductsUiState.Success -> {
                val products = (uiState as ProductsUiState.Success).products
                if (products.isNotEmpty()) {
                    ProductList(
                        innerPadding = innerPadding,
                        products = products
                    )
                } else {
                    ErrorView(stringResource(R.string.txt_no_products))
                }
            }
        }

        /** ADD PRODUCT **/

        if (showBottomSheet) {
            AddProductBottomSheet(
                sheetState = sheetState,
                onDismiss = {
                    showBottomSheet = false
                },
                onInserted = { product ->
                    viewModel.insertProduct(product)
                }
            )
        }

        if (loadingState == true) {
            Dialog(
                onDismissRequest = { /*TODO*/ },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            ) {
                LoadingView()
            }
        }

    }

}

/** Components **/

@Composable
fun ProductList(
    innerPadding: PaddingValues,
    products: List<Product>,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = innerPadding.calculateTopPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding()
            )
    ) {
        items(
            items = products,
            key = { it.id ?: -1 }
        ) { p ->
            ProductItem(p)
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            //Product icon
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Product icon",
                modifier = Modifier
                    .size(40.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                //Product name
                Text(text = product.name)
                //Product stock
                Text(text = "Stock: ${product.stock}")
            }

        }
    }
}

@Composable
fun ProductsFAB(onAdd: () -> Unit) {
    FloatingActionButton(
        onClick = { onAdd() }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add product"
        )
    }
}