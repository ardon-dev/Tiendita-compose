package com.ardondev.tiendita.presentation.screens.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.R
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.presentation.util.CustomTextField
import com.ardondev.tiendita.presentation.util.DECIMAL_PATTERN
import com.ardondev.tiendita.presentation.util.IconSelector
import com.ardondev.tiendita.presentation.util.formatToUSD
import com.ardondev.tiendita.presentation.util.getOnlyDigits


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddProductsBottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
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
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = { }
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            ProductForm(
                modifier = Modifier.fillMaxWidth(),
                onInserted = { product ->
                    onInserted(product)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductForm(
    modifier: Modifier,
    onInserted: (product: Product) -> Unit,
) {

    var nameState by remember { mutableStateOf("") }
    var validName by remember { mutableStateOf(false) }
    var stockState by remember { mutableStateOf("") }
    var validStock by remember { mutableStateOf(false) }
    var priceState by remember { mutableStateOf("") }
    var validPrice by remember { mutableStateOf(false) }
    var iconResId by remember {
        mutableIntStateOf(icons[0])
    }

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        BottomSheetDefaults.DragHandle(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Agregar producto",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )

        //Name
        CustomTextField(
            value = nameState,
            labelText = stringResource(R.string.txt_name),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            onValueChange = { newValue ->
                nameState = newValue
                validName = nameState.isNotEmpty()
            },
            modifier = modifier,
            placeHolderText = "Ingresa nombre"
        )

        Spacer(Modifier.size(16.dp))

        //Stock
        CustomTextField(
            value = stockState,
            singleLine = true,
            labelText = stringResource(R.string.txt_initial_stock),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            onValueChange = { newValue ->
                val digits = getOnlyDigits(newValue)
                stockState = if (digits.startsWith("0")) "1" else digits
                validStock = stockState.isNotEmpty() && (stockState.toInt()) > 0
            },
            placeHolderText = "Ingresa el stock inicial"
        )

        Spacer(Modifier.size(16.dp))

        //Price
        CustomTextField(
            value = priceState,
            singleLine = true,
            suffixText = "c/u",
            placeHolderText = "0.00",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            onValueChange = { newValue ->
                if (newValue.matches(DECIMAL_PATTERN)) {
                    priceState = newValue
                }
                validPrice = priceState.isNotEmpty() && (priceState.toDouble()) > 0
            },
            leadingIcon = Icons.Default.AttachMoney,
            labelText = "Precio"
        )

        //Icon
        Text(
            text = "Ícono",
            style = MaterialTheme.typography.labelMedium,
            modifier = modifier
                .padding(
                    start = 12.dp,
                    bottom = 6.dp,
                    top = 16.dp
                )
        )

        IconSelector(
            iconsResIds = icons,
            onClick = { resId ->
                iconResId = resId
            }
        )

        //Button
        Button(
            enabled = validName && validStock && validPrice,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                val product = Product(
                    id = null,
                    name = nameState,
                    price = priceState.toDouble(),
                    stock = stockState.toInt(),
                    totalSales = null,
                    resId = iconResId
                )
                onInserted(product)
            }
        ) {
            Text("Agregar")
        }

    }

}

val icons = listOf(
    R.drawable.ic_candy,
    R.drawable.ic_chocolate,
    R.drawable.ic_cookies,
    R.drawable.ic_cup_cake,
    R.drawable.ic_donuts,
    R.drawable.ic_gummy,
    R.drawable.ic_gummy_bear,
    R.drawable.ic_ice_cream,
    R.drawable.ic_ice_cream_stick,
    R.drawable.ic_ice_cream_stick_2,
    R.drawable.ic_lollipop,
    R.drawable.ic_lollipop_2,
    R.drawable.ic_lollipop_3,
)


