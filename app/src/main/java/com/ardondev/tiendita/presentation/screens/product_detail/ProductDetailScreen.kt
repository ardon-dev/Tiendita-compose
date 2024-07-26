package com.ardondev.tiendita.presentation.screens.product_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.ardondev.tiendita.presentation.util.MMMM_d
import com.ardondev.tiendita.presentation.util.SingleEvent
import com.ardondev.tiendita.presentation.util.formatDate
import com.ardondev.tiendita.presentation.util.yyyy_MM_dd
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {

    val scope = rememberCoroutineScope()
    var datePickerState = rememberDatePickerState()
    var showDatePicker by remember {
        mutableStateOf(false)
    }
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
                title = viewModel.product?.name.orEmpty(),
                viewModel = viewModel,
                onDateClick = {
                    showDatePicker = true
                }
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
                    LeadingIconTab(
                        selected = viewModel.tabPosition == index,
                        onClick = { viewModel.setTabPositionValue(index) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Rounded.PointOfSale else Icons.Rounded.Category,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = name,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.setStartDateValue(millis.formatMillis())
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = "Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }

}

private fun Long.formatMillis(): String {
    val time =
        SimpleDateFormat(yyyy_MM_dd, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date(this))
    return time
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailTopAppBar(
    navHostController: NavHostController,
    title: String,
    viewModel: ProductDetailViewModel,
    onDateClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(title)
        },
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
        },
        actions = {
            if (viewModel.tabPosition == 0) {
                DateFilter(
                    onDateClick = onDateClick,
                    viewModel = viewModel
                )
            }
        }
    )
}

@Composable
fun DateFilter(
    onDateClick: () -> Unit,
    viewModel: ProductDetailViewModel,
) {
    AssistChip(
        onClick = {
            onDateClick()
        },
        label = {
            val startDate = formatDate(
                input = viewModel.startDate.collectAsState().value,
                inputFormat = yyyy_MM_dd,
                outputFormat = MMMM_d
            )
            val endDate = formatDate(viewModel.endDate.collectAsState().value, yyyy_MM_dd, MMMM_d)
            val dateText = if (startDate == endDate) startDate else "$startDate - $endDate"
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.DateRange,
                contentDescription = null,
                Modifier.size(18.dp)
            )
        },
        modifier = Modifier.padding(end = 16.dp)
    )
}

@Composable
fun ProductDetailBottomAppBar(
    viewModel: ProductDetailViewModel,
) {
    BottomAppBar(
        actions = {
            if (viewModel.tabPosition == 0) {
                Text(
                    text = "Ingreso total: $${viewModel.totalSales.collectAsState().value}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    state: DatePickerState,
) {

}


