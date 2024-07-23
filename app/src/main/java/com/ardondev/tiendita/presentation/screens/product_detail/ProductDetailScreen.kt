package com.ardondev.tiendita.presentation.screens.product_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ardondev.tiendita.R
import com.ardondev.tiendita.presentation.screens.product_detail.sales.SalesScreen
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.LoadingView
import com.ardondev.tiendita.presentation.util.SingleEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by produceState<ProductDetailUiState>(
        initialValue = ProductDetailUiState.Loading, key1 = lifecycle, key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { value = it }
        }
    }

    LaunchedEffect(productId) {
        viewModel.savedStateHandle["product_id"] = productId
    }

    /** Events **/

    viewModel.updateProductResult.observe(LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { rows ->
            rows?.let {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        if (rows > 0) "Updated" else "Not updated"
                    )
                }
            }
        })

    viewModel.updateProductError.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { error ->
            error?.message?.let { message ->
                scope.launch { snackBarHostState.showSnackbar(message) }
            }
        })

    /** Product detail **/

    Scaffold(
        topBar = { ProductDetailTopAppBar() },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Text(
                        text = "Ingresos: $${viewModel.totalSales}",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                floatingActionButton = {
                    if (!viewModel.loading) {
                        ProductDetailFAB(viewModel.fabIcon) {
                            viewModel.setEditableValue(!viewModel.editable)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            //TABS
            PrimaryTabRow(
                selectedTabIndex = viewModel.tabPosition, modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Ventas", "Producto").forEachIndexed { index, name ->
                    Tab(
                        selected = viewModel.tabPosition == index,
                        onClick = { viewModel.setTabPositionValue(index) },
                        text = {
                            Text(
                                text = name, maxLines = 2, overflow = TextOverflow.Ellipsis
                            )
                        })
                }
            }

            //CONTENT
            Box(Modifier.fillMaxSize()) {
                when (viewModel.tabPosition) {

                    //PRODUCT DETAIL
                    0 -> SalesScreen(
                        snackBarHostState = snackBarHostState
                    )

                    //SELLS LIST
                    else -> ProductDetailContent(
                        viewModel = viewModel, uiState = uiState
                    )

                }
            }
        }
    }

}

@Composable
fun ProductDetailContent(
    viewModel: ProductDetailViewModel,
    uiState: ProductDetailUiState,
) {
    when (uiState) {
        is ProductDetailUiState.Error -> {
            val error = (uiState as ProductDetailUiState.Error).message
            ErrorView(error)
        }

        ProductDetailUiState.Loading -> {
            LoadingView()
        }

        is ProductDetailUiState.Success -> {
            ProductDetailForm(
                viewModel = viewModel, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailTopAppBar() {
    TopAppBar(title = {
        Text(text = stringResource(R.string.txt_product_detail))
    }, navigationIcon = {
        IconButton(onClick = {

        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
            )
        }
    })
}

@Composable
fun ProductDetailFAB(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
    ) {
        Icon(
            imageVector = icon, contentDescription = "Edit"
        )
    }
}

@Composable
fun ProductDetailForm(
    modifier: Modifier,
    viewModel: ProductDetailViewModel,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        if (viewModel.loading) {
            LoadingView()
        }

        //Name
        OutlinedTextField(
            value = viewModel.name,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Text
            ),
            onValueChange = viewModel::setNameValue,
            label = { Text(stringResource(R.string.txt_name)) },
            modifier = modifier,
            enabled = viewModel.editable
        )

        //Stock
        OutlinedTextField(
            value = viewModel.stock,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            onValueChange = viewModel::setStockValue,
            label = { Text(stringResource(R.string.txt_stock)) },
            modifier = modifier.padding(top = 16.dp),
            enabled = viewModel.editable
        )

        //Price
        OutlinedTextField(
            value = viewModel.price,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
            ),
            onValueChange = viewModel::setPriceValue,
            prefix = { Text("$") },
            suffix = { Text("c/u") },
            label = { Text(stringResource(R.string.txt_price)) },
            modifier = modifier.padding(top = 16.dp),
            enabled = viewModel.editable,
        )

    }
}
