package com.ardondev.tiendita.presentation.products

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ardondev.tiendita.R
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.LoadingView
import kotlinx.coroutines.coroutineScope
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

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel,
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by produceState<ProductsUiState>(
        initialValue = ProductsUiState.Loading,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { value = it }
        }
    }

    /** Products **/

    Scaffold(
        //ADD FAB
        floatingActionButton = {
            ProductsFAB(
                onAdd = {
                    viewModel.insertProduct(Product(null, "Piruleta", 10, 0.10))
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        /** Handle results **/

        //CONTENT
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
    }

    /** Events: Insert product **/

    val insertResultState = viewModel.insertProductResult.observeAsState()
    val insertErrorState = viewModel.insertProductError.observeAsState()

    LaunchedEffect(insertResultState.value) {
        insertResultState.value?.let { id ->
            scope.launch { snackBarHostState.showSnackbar("Inserted: $id") }
        }
    }

    LaunchedEffect(insertErrorState.value) {
        insertErrorState.value?.let { message ->
            scope.launch { snackBarHostState.showSnackbar(message) }
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
        items(products) { p ->
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