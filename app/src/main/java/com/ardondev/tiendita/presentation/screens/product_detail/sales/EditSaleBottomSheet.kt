package com.ardondev.tiendita.presentation.screens.product_detail.sales

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.domain.model.Sale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSaleBottomSheet(
    sale: Sale,
    stock: Int,
    currentQuantity: Int,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    onUpdated: (Sale) -> Unit,
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
                    text = "Actualizar venta",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                )

                SaleForm(
                    price = sale.amount,
                    stock = stock,
                    modifier = Modifier.fillMaxWidth(),
                    onInserted = { price, quantity, byUnity ->
                        Log.d("TAG2", "price: ${price}, quantity: ${quantity}")
                        onUpdated(
                            sale.copy(
                                amount = price,
                                byUnity = byUnity,
                                quantity = quantity,
                                total = if (byUnity) (price * quantity) else price
                            )
                        )
                    },
                    currentQuantity = currentQuantity
                )

            }
        }
    }

}