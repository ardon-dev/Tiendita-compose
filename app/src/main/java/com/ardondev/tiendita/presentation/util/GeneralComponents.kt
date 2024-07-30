package com.ardondev.tiendita.presentation.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    CustomTextField(
        value = value.value,
        onValueChange = { value.value = it },
        labelText = stringResource(R.string.txt_quantity),
        leadingIcon = Icons.Rounded.Remove,
        leadingIconClick = onMinus,
        trailingIcon = Icons.Rounded.Add,
        trailingIconClick = onPlus,
        iconsTint = MaterialTheme.colorScheme.primary,
        textStyle = TextStyle(
            textAlign = TextAlign.Center
        ),
        supportingText = {
            val aux = stock - quantity
            Text(
                text = "Stock: $aux"
            )
        },
        modifier = modifier
            .focusProperties { canFocus = false }
    )
}

@Composable
fun CustomTextField(
    labelText: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    leadingIconClick: () -> Unit = {},
    trailingIcon: ImageVector? = null,
    trailingIconClick: () -> Unit = {},
    singleLine: Boolean = false,
    suffixText: String? = null,
    prefixText: String? = null,
    placeHolderText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    supportingText: (@Composable () -> Unit?)? = null,
    enabled: Boolean = true,
    iconsTint: Color? = null,
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // LABEL //
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium,
            modifier = modifier
                .padding(
                    start = 12.dp,
                    bottom = 6.dp
                )
        )
        //TEXT FIELD //
        OutlinedTextField(
            value = value,
            enabled = enabled,
            singleLine = singleLine,
            suffix = suffixText?.let {
                {
                    Text(it)
                }
            },
            prefix = prefixText?.let {
                {
                    Text(it)
                }
            },
            placeholder = placeHolderText?.let {
                {
                    Text(it)
                }
            },
            keyboardOptions = keyboardOptions,
            onValueChange = { onValueChange(it) },
            modifier = modifier.fillMaxWidth(),
            leadingIcon = leadingIcon?.let {
                {
                    IconButton(leadingIconClick) {
                        Icon(
                            imageVector = it,
                            contentDescription = "",
                            tint = iconsTint ?: MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            trailingIcon = trailingIcon?.let {
                {
                    IconButton(trailingIconClick) {
                        Icon(
                            imageVector = it,
                            contentDescription = "",
                            tint = iconsTint ?: MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            supportingText = supportingText?.let {
                {
                    supportingText()
                }
            },
            textStyle = textStyle,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}