package com.ardondev.tiendita.presentation.screens.product_detail.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.R
import com.ardondev.tiendita.presentation.screens.product_detail.ProductDetailViewModel
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        if (viewModel.loading) {
            LoadingView()
        }

        //Name
        OutlinedTextField(
            value = viewModel.name,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Text
            ),
            onValueChange = viewModel::setNameValue,
            label = { Text(stringResource(R.string.txt_name)) },
            modifier = modifier,
            enabled = viewModel.editable
        )

        //Stock
        OutlinedTextField(
            value = viewModel.stock,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            onValueChange = viewModel::setStockValue,
            label = { Text(stringResource(R.string.txt_stock)) },
            modifier = modifier.padding(top = 16.dp),
            enabled = viewModel.editable
        )

        //Price
        OutlinedTextField(
            value = viewModel.price,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
            ),
            onValueChange = viewModel::setPriceValue,
            prefix = { Text("$") },
            suffix = { Text("c/u") },
            label = { Text(stringResource(R.string.txt_price)) },
            modifier = modifier.padding(top = 16.dp),
            enabled = viewModel.editable,
        )

    }
}