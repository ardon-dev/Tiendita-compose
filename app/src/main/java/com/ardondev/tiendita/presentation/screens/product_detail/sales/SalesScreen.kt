package com.ardondev.tiendita.presentation.screens.product_detail.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.presentation.screens.product_detail.ProductDetailViewModel
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.LoadingView
import com.ardondev.tiendita.presentation.util.SingleEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState,
) {

    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
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

    viewModel.insertSaleResult.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { id ->
            id?.let { scope.launch { snackBarHostState.showSnackbar("Inserted: $id") } }
        }
    )

    viewModel.insertSaleError.observe(
        LocalLifecycleOwner.current,
        SingleEvent.SingleEventObserver { error ->
            error?.message?.let { message ->
                scope.launch { snackBarHostState.showSnackbar(message) }
            }
        })

    /** Sales UI **/

    when (uiState) {
        is SalesUiState.Error -> {
            val message = (uiState as SalesUiState.Error).message
            ErrorView(message)
        }

        SalesUiState.Loading -> LoadingView()

        is SalesUiState.Success -> {
            val sales = (uiState as SalesUiState.Success).sales
            SalesList(sales)
        }
    }

    /** Add sale **/

    if (viewModel.showBottomSheet) {
        AddSaleBottomSheet(
            price = viewModel.product?.price ?: 0.0,
            stock = viewModel.product?.stock ?: 0,
            sheetState = sheetState,
            onDismiss = { viewModel.setShowBottomSheetValue(false) },
            onInserted = { price, quantity ->
                viewModel.insertSale(price, quantity)
            }
        )
    }

}

@Composable
fun SalesList(sales: List<Sale>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
    ) {
        items(
            items = sales,
            key = { it.id ?: -1 }
        ) { sale ->
            SaleItem(sale)
        }
    }
}

@Composable
fun SaleItem(sale: Sale) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            //Icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Sale icon",
            )

            Spacer(Modifier.size(16.dp))

            //Info
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //Sale price
                Text(
                    text = "Precio de venta: $${sale.amount}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )

                //Quantity
                Text(
                    text = "Cantidad: ${sale.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )

                //Total
                Text(
                    text = "Total: $${sale.total}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )

            }

        }
    }
}

@Preview
@Composable
fun SaleItemPreview() {
    SaleItem(sale = Sale.getEmptySale())
}