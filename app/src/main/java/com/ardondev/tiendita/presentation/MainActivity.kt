package com.ardondev.tiendita.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ardondev.tiendita.presentation.products.ProductsScreen
import com.ardondev.tiendita.presentation.products.ProductsViewModel
import com.ardondev.tiendita.presentation.theme.TienditaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val productsViewModel: ProductsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TienditaTheme {
                ProductsScreen(productsViewModel)
            }
        }

    }

    @Preview
    @Composable
    fun MainPreview() {
        ProductsScreen(productsViewModel)
    }

}