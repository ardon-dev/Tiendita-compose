package com.ardondev.tiendita.presentation.screens.product_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ardondev.tiendita.domain.model.Sale

@Composable
fun SalesScreen(
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

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
                    text = "Cantidad: $${sale.quantity}",
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