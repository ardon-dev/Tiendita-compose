package com.ardondev.tiendita.presentation.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.ardondev.tiendita.R

@Composable
fun ErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(message)
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun QuantityControl(
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    quantity: Int,
    stock: Int,
    modifier: Modifier,
    value: MutableState<String>,
) {
    OutlinedTextField(
        value = value.value,
        onValueChange = { value.value = it },
        label = { Text(stringResource(R.string.txt_quantity)) },
        leadingIcon = {
            IconButton(onClick = {
                onMinus()
            }) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Minus",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                onPlus()
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Plus",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        textStyle = TextStyle(
            textAlign = TextAlign.Center
        ),
        supportingText = {
            Text("Stock: ${stock - quantity}")
        },
        modifier = modifier
            .focusProperties {
                canFocus = false
            }
    )
}