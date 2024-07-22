package com.ardondev.tiendita.presentation.screens.product_detail

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {

    LaunchedEffect(productId) {
        viewModel.savedStateHandle["product_id"] = productId
    }

    val data = viewModel.productId
    Log.d("producto", "pid: $data")

}