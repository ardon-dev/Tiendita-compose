package com.ardondev.tiendita.presentation.screens.product_detail.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ardondev.tiendita.R
import com.ardondev.tiendita.domain.model.Sale
import com.ardondev.tiendita.presentation.screens.product_detail.ProductDetailViewModel
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.LoadingView
import com.ardondev.tiendita.presentation.util.MMMM_d_yyyy_h_mm_a
import com.ardondev.tiendita.presentation.util.SingleEvent
import com.ardondev.tiendita.presentation.util.formatDate
import com.ardondev.tiendita.presentation.util.formatToUSD
import kotlinx.coroutines.launch
import java.lang.Error

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
            if (sales.isNotEmpty()) {
                SalesList(sales)
            } else {
                ErrorView("No hay ventas.")
            }
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
        contentPadding = PaddingValues(16.dp)
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
                .padding(
                    vertical = 10.dp,
                    horizontal = 16.dp
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            //Icon
            Text(
                text = "\uD83D\uDCB2",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(Modifier.size(16.dp))

            //Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                //Sale date
                val date = formatDate(input = sale.date, outputFormat = MMMM_d_yyyy_h_mm_a)
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth()
                )

                //Quantity
                Text(
                    text = stringResource(R.string.txt_sold_quantity, sale.quantity),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )

                //Price unity
                Text(
                    text = "Precio c/u: $${formatToUSD(sale.amount.toString())}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )

            }

            //Total
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "+ $${formatToUSD(sale.total.toString())}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimary
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