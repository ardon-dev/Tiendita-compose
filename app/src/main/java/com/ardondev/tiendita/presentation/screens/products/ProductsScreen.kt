package com.ardondev.tiendita.presentation.screens.products

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.ardondev.tiendita.R
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.presentation.Routes
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.LoadingView
import com.ardondev.tiendita.presentation.util.SingleEvent
import com.ardondev.tiendita.presentation.util.formatToUSD
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
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
        SingleEvent.SingleEventObserver { _ ->
            showBottomSheet = false
        })

    viewModel.insertProductError.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { error ->
            error?.let {
                scope.launch {
                    snackBarHostState.showSnackbar(error)
                }
            }
            showBottomSheet = false
        })

    /** Products **/

    Scaffold(
        topBar = { ProductsTopAppBar(viewModel.totalSales) },
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
                        products = products,
                        totalSales = viewModel.totalSales,
                        onProductClick = { productId ->
                            navHostController.navigate(
                                Routes.ProductDetailScreen.createRoute(
                                    productId
                                )
                            )
                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsTopAppBar(totalSales: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TopAppBar({ Text("Tiendita") })
        ProductsHeader(totalSales = totalSales)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductList(
    innerPadding: PaddingValues,
    products: List<Product>,
    totalSales: String,
    onProductClick: (productId: Long) -> Unit,
) {
    // LIST //
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
            ProductItem(
                product = p,
                onClick = {
                    onProductClick(p.id ?: 0L)
                }
            )
        }
    }
}

@Composable
fun ProductsHeader(
    totalSales: String,
) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Text(
                text = "Ingreso total: $${totalSales}",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {

            //Product icon
            Icon(
                imageVector = Icons.Rounded.Category,
                contentDescription = "",
                modifier = Modifier.size(40.dp).align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                //Product name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                //Product stock
                Text(
                    text = "Stock: ${product.stock}",
                    style = MaterialTheme.typography.bodyMedium
                )
                //Product price
                Text(
                    text = "Precio unitario: $${formatToUSD(product.price.toString())}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = "$${formatToUSD(product.totalSales.toString())}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(
                        vertical = 4.dp,
                        horizontal = 6.dp
                    )
                )
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