package com.ardondev.tiendita.presentation.screens.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.R
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.presentation.util.formatToUSD
import com.ardondev.tiendita.presentation.util.getOnlyDigits


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddProductsBottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState()
    AddProductBottomSheet(
        sheetState = sheetState,
        onDismiss = {

        },
        onInserted = {

        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onInserted: (product: Product) -> Unit,
) {

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {

        ProductForm(
            modifier = Modifier.fillMaxWidth(),
            onInserted = { product ->
                onInserted(product)
            }
        )
    }
}

@Composable
fun ProductForm(
    modifier: Modifier,
    onInserted: (product: Product) -> Unit,
) {

    var nameState by remember { mutableStateOf(TextFieldValue("")) }
    var validName by remember { mutableStateOf(false) }
    var stockState by remember { mutableStateOf(TextFieldValue("")) }
    var validStock by remember { mutableStateOf(false) }
    var priceState by remember { mutableStateOf(TextFieldValue("")) }
    var validPrice by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
    ) {

        //Title
        Text(
            text = stringResource(R.string.txt_add_product),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier.size(16.dp))

        //Name
        OutlinedTextField(
            value = nameState,
            label = { Text(stringResource(R.string.txt_name)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            onValueChange = { newValue ->
                nameState = newValue
                validName = newValue.text.isNotEmpty()
            },
            modifier = modifier
        )

        //Stock
        OutlinedTextField(
            value = stockState,
            singleLine = true,
            label = { Text(stringResource(R.string.txt_initial_stock)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            onValueChange = { newValue ->
                val digits = getOnlyDigits(newValue.text)
                stockState = newValue.copy(
                    text = digits,
                    selection = TextRange(digits.length)
                )
                validStock = digits.isNotEmpty() && (digits.toInt()) > 0
            },
            modifier = modifier
                .padding(top = 16.dp)
        )

        //Price
        OutlinedTextField(
            value = priceState,
            singleLine = true,
            prefix = { Text(text = "$") },
            suffix = { Text(text = "c/u") },
            label = { Text(stringResource(R.string.txt_price)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            onValueChange = { newValue ->
                val amount = formatToUSD(newValue.text)
                priceState = newValue.copy(
                    text = amount
                )
                validPrice = amount.isNotEmpty() && (amount.toDouble()) > 0
            },
            modifier = modifier
                .padding(top = 16.dp)
        )

        //Button
        Button(
            enabled = validName && validStock && validPrice,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                val product = Product(
                    id = null,
                    name = nameState.text,
                    price = priceState.text.toDouble(),
                    stock = stockState.text.toInt()
                )
                onInserted(product)
            }
        ) {
            Text(text = "Agregar")
        }

    }

}


