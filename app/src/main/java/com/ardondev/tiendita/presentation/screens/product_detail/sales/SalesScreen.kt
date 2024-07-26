package com.ardondev.tiendita.presentation.screens.product_detail.sales

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.presentation.screens.product_detail.ProductDetailViewModel
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.HH_mm_ss
import com.ardondev.tiendita.presentation.util.LoadingView
import com.ardondev.tiendita.presentation.util.SingleEvent
import com.ardondev.tiendita.presentation.util.formatTime
import com.ardondev.tiendita.presentation.util.formatToUSD
import com.ardondev.tiendita.presentation.util.h_mm_a
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState,
) {

    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val addSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val editSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val uiState by produceState<SalesUiState>(
        initialValue = SalesUiState.Loading,
        key1 = lifecycle,
        key2 = viewModel,
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.salesUiState.collect { value = it }
        }
    }

    /** Insert sale events **/

    viewModel.insertSaleError.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { error ->
            error?.message?.let { message ->
                scope.launch { snackBarHostState.showSnackbar(message) }
            }
        })

    /** Update sale events **/

    viewModel.updateSaleError.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { error ->
            error?.message?.let {
                scope.launch { snackBarHostState.showSnackbar(it) }
            }
        }
    )

    /** Sales UI **/

    when (uiState) {
        is SalesUiState.Error -> {
            val message = (uiState as SalesUiState.Error).message
            ErrorView(message)
        }

        SalesUiState.Loading -> LoadingView()

        is SalesUiState.Success -> {
            val sales = (uiState as SalesUiState.Success).sales
            if (sales.isNotEmpty()) {
                SalesList(
                    sales = sales,
                    onSaleClick = { s ->
                        viewModel.setSaleSelectedValue(s)
                        viewModel.setEditBottomSheetValue(true)
                    },
                    onDelete = { s ->
                        viewModel.deleteSale(s)
                    }
                )
            } else {
                ErrorView("No hay ventas.")
            }
        }
    }

    /** Add sale **/

    if (viewModel.showAddBottomSheet) {
        AddSaleBottomSheet(
            price = viewModel.product?.price ?: 0.0,
            stock = viewModel.product?.stock ?: 0,
            sheetState = addSheetState,
            onDismiss = { viewModel.setShowAddBottomSheetValue(false) },
            onInserted = { price, quantity ->
                viewModel.insertSale(price, quantity)
            }
        )
    }

    if (viewModel.showEditBottomSheet) {
        viewModel.saleSelected?.let { saleSelected ->
            EditSaleBottomSheet(
                sale = saleSelected,
                stock = viewModel.product?.stock ?: 0,
                onDismiss = {
                    viewModel.setEditBottomSheetValue(false)
                },
                sheetState = editSheetState,
                onUpdated = { sale ->
                    Log.d("TAG3", "price: ${sale.amount}, quantity: ${sale.quantity}")
                    viewModel.updateSale(
                        sale = sale,
                        oldQuantity = saleSelected.quantity
                    )
                },
                currentQuantity = saleSelected.quantity
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SalesList(
    sales: List<Sale>,
    onSaleClick: (Sale) -> Unit,
    onDelete: (Sale) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        )
    ) {
        stickyHeader {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                val total = formatToUSD(sales.sumOf { it.total }.toString())
                Text(
                    text = "Ingresos de este día: $$total",
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
        items(
            items = sales,
            key = { it.id ?: -1 }
        ) { sale ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    when (it) {
                        SwipeToDismissBoxValue.EndToStart -> {
                            onDelete(sale)
                        }

                        else -> return@rememberSwipeToDismissBoxState false
                    }
                    return@rememberSwipeToDismissBoxState true
                },
                positionalThreshold = { it * .50f }
            )

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(8.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        )
                    }
                },
                content = {
                    SaleItem(sale) {
                        onSaleClick(sale)
                    }
                }
            )
        }
    }
}

@Composable
fun SaleItem(
    sale: Sale,
    onSaleClick: (Sale) -> Unit,
) {
    Card(
        onClick = {
            onSaleClick(sale)
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 10.dp,
                    horizontal = 16.dp
                )
        ) {

            Icon(
                imageVector = Icons.Rounded.MonetizationOn,
                contentDescription = null
            )

            //Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {

                Text(
                    text = "Cantidad: ${sale.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )

                Text(
                    text = "Precio: $${formatToUSD(sale.amount.toString())} c/u",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = "",
                        modifier = Modifier
                            .size(12.dp)
                            .padding(bottom = 1.dp)
                    )
                    Text(
                        text = formatTime(
                            input = sale.time,
                            inputFormat = HH_mm_ss,
                            outputFormat = h_mm_a
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

            }

            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = "+ $${formatToUSD(sale.total.toString())}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        vertical = 4.dp,
                        horizontal = 6.dp
                    )
                )
            }

        }
    }
}