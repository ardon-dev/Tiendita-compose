package com.ardondev.tiendita.presentation.screens.product_detail.sales

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.R
import com.ardondev.tiendita.presentation.util.CustomTextField
import com.ardondev.tiendita.presentation.util.DECIMAL_PATTERN
import com.ardondev.tiendita.presentation.util.QuantityControl
import com.ardondev.tiendita.presentation.util.formatToUSD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleBottomSheet(
    price: Double,
    stock: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onInserted: (price: Double, quantity: Int, byUnity: Boolean) -> Unit,
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

                Text(
                    text = "Nueva venta",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                )

                SaleForm(price = price,
                    stock = stock,
                    modifier = Modifier.fillMaxWidth(),
                    onInserted = { price, quantity, byUnity ->
                        onInserted(price, quantity, byUnity)
                    },
                    currentQuantity = null
                )
            }
        }
    }

}

@Composable
fun SaleForm(
    price: Double,
    stock: Int,
    modifier: Modifier,
    onInserted: (price: Double, quantity: Int, byUnity: Boolean) -> Unit,
    currentQuantity: Int? = null,
    byUnity: Boolean? = null
) {

    var priceState by remember { mutableStateOf(formatToUSD(price.toString())) }
    var validPrice by remember { mutableStateOf(true) }
    var quantityState = remember {
        mutableStateOf(
            currentQuantity?.toString() ?: "1"
        )
    }
    var quantity by remember { quantityState }
    val newStock = if (currentQuantity != null) (stock + currentQuantity.toInt()) else stock //7
    var byUnity by remember { mutableStateOf(byUnity ?: false) }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
    ) {

        //

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Box(
                Modifier.weight(1f)
            ) {
                //Price
                CustomTextField(
                    value = priceState,
                    onValueChange = { newValue ->
                        if (newValue.matches(DECIMAL_PATTERN)) {
                            priceState = newValue
                        }
                        validPrice = (priceState.isNotEmpty()) && (priceState.toDouble() > 0.0)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    labelText = stringResource(R.string.txt_sale_price),
                    singleLine = true,
                    leadingIcon = Icons.Default.AttachMoney,
                    modifier = modifier
                )
            }

            Spacer(Modifier.size(16.dp))

            FilterChip(
                selected = byUnity,
                onClick = { byUnity = !byUnity },
                label = {
                    Text(
                        text = "c/u"
                    )
                },
                modifier = Modifier.padding(top = 20.dp)
            )


        }

        Spacer(Modifier.size(16.dp))

        //Quantity control
        QuantityControl(
            onMinus = {
                if (quantity.toInt() > 1) {
                    quantity = quantity.toInt().minus(1).toString()
                }
            },
            onPlus = {
                if (quantity.toInt() < newStock) {
                    quantity = quantity.toInt().plus(1).toString()
                }
            },
            quantity = quantity.toInt(),
            stock = newStock,
            modifier = modifier,
            value = quantityState
        )

        //Button
        Button(
            enabled = validPrice,
            onClick = {
                Log.d("TAG", "price: ${priceState}, quantity: ${quantity}")
                onInserted(priceState.toDouble(), quantity.toInt(), byUnity)
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(if (currentQuantity != null) "Actualizar" else "Agregar")
        }

    }

}