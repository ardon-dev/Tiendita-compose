package com.ardondev.tiendita.presentation.screens.product_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.ardondev.tiendita.presentation.screens.product_detail.product.ProductScreen
import com.ardondev.tiendita.presentation.screens.product_detail.product.ProductUiState
import com.ardondev.tiendita.presentation.screens.product_detail.sales.SalesScreen
import com.ardondev.tiendita.presentation.util.SingleEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by produceState<ProductUiState>(
        initialValue = ProductUiState.Loading, key1 = lifecycle, key2 = viewModel
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
        topBar = {
            ProductDetailTopAppBar(
                navHostController = navHostController,
                title = viewModel.product?.name.orEmpty()
            )
        },
        bottomBar = { ProductDetailBottomAppBar(viewModel) },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Tabs //

            PrimaryTabRow(
                selectedTabIndex = viewModel.tabPosition,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Ventas", "Producto").forEachIndexed { index, name ->
                    Tab(
                        selected = viewModel.tabPosition == index,
                        onClick = { viewModel.setTabPositionValue(index) },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val emoji = if (index == 0) "\uD83D\uDCB8" else "\uD83C\uDF6C"
                                Text(
                                    text = "$emoji  $name",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    )
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
                    else -> ProductScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )

                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailTopAppBar(
    navHostController: NavHostController,
    title: String,
) {
    TopAppBar(title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = {
                    navHostController.navigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
fun ProductDetailBottomAppBar(
    viewModel: ProductDetailViewModel,
) {
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


