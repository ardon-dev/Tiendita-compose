package com.ardondev.tiendita.presentation.screens.product_detail.sales

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.R
import com.ardondev.tiendita.presentation.util.CustomTextField
import com.ardondev.tiendita.presentation.util.QuantityControl
import com.ardondev.tiendita.presentation.util.formatToUSD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleBottomSheet(
    price: Double,
    stock: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onInserted: (price: Double, quantity: Int) -> Unit,
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {

                BottomSheetDefaults.DragHandle(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                /*
                Text(
                    text = stringResource(R.string.txt_add_sale),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )

                HorizontalDivider(Modifier.padding(top = 16.dp))

                 */

                SaleForm(price = price,
                    stock = stock,
                    modifier = Modifier.fillMaxWidth(),
                    onInserted = { price, quantity ->
                        onInserted(price, quantity)
                    })
            }
        }
    }

}

@Composable
fun SaleForm(
    price: Double,
    stock: Int,
    modifier: Modifier,
    onInserted: (price: Double, quantity: Int) -> Unit,
) {

    var priceState by remember { mutableStateOf(formatToUSD(price.toString())) }
    var validPrice by remember { mutableStateOf(true) }
    var quantityState = remember { mutableStateOf("1") }
    var quantity by remember { quantityState }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
    ) {

        //Price
        CustomTextField(
            value = priceState,
            onValueChange = {
                priceState = formatToUSD(it.ifEmpty { "1" })
                validPrice = (priceState.isNotEmpty()) && (priceState.toDouble() > 0.0)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            labelText = stringResource(R.string.txt_sale_price),
            singleLine = true,
            leadingIcon = Icons.Default.AttachMoney,
            suffixText = "c/u",
            modifier = modifier
        )

        Spacer(Modifier.size(16.dp))

        //Quantity control
        QuantityControl(
            onMinus = {
                if (quantity.toInt() > 1) {
                    quantity = quantity.toInt().minus(1).toString()
                }
            },
            onPlus = {
                if (quantity.toInt() < stock) {
                    quantity = quantity.toInt().plus(1).toString()
                }
            },
            quantity = quantity.toInt(),
            stock = stock,
            modifier = modifier,
            value = quantityState
        )

        //Button
        Button(
            enabled = validPrice,
            onClick = {
                onInserted(priceState.toDouble(), quantity.toInt())
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Agregar")
        }

    }

}