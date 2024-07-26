package com.ardondev.tiendita.presentation.screens.product_detail.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.R
import com.ardondev.tiendita.presentation.screens.product_detail.ProductDetailViewModel
import com.ardondev.tiendita.presentation.util.CustomTextField
import com.ardondev.tiendita.presentation.util.ErrorView
import com.ardondev.tiendita.presentation.util.LoadingView

@Composable
fun ProductScreen(
    viewModel: ProductDetailViewModel,
    uiState: ProductUiState,
) {
    when (uiState) {
        is ProductUiState.Error -> {
            val error = (uiState as ProductUiState.Error).message
            ErrorView(error)
        }

        ProductUiState.Loading -> {
            LoadingView()
        }

        is ProductUiState.Success -> {
            EditableProductForm(
                viewModel = viewModel, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EditableProductForm(
    modifier: Modifier,
    viewModel: ProductDetailViewModel,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        //Name
        CustomTextField(
            value = viewModel.name,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Text
            ),
            onValueChange = viewModel::setNameValue,
            labelText = stringResource(R.string.txt_name),
            modifier = modifier,
            enabled = viewModel.editable
        )

        Spacer(Modifier.size(16.dp))

        //Stock
        CustomTextField(
            value = viewModel.stock,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            onValueChange = viewModel::setStockValue,
            labelText = stringResource(R.string.txt_stock),
            enabled = viewModel.editable
        )

        Spacer(Modifier.size(16.dp))

        //Price
        CustomTextField(
            value = viewModel.price,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
            ),
            onValueChange = viewModel::setPriceValue,
            leadingIcon = Icons.Rounded.AttachMoney,
            suffixText = "c/u",
            labelText = stringResource(R.string.txt_price),
            enabled = viewModel.editable,
        )

    }
}