package com.ardondev.tiendita.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ardondev.tiendita.presentation.screens.products.ProductsScreen
import com.ardondev.tiendita.presentation.screens.products.ProductsViewModel
import com.ardondev.tiendita.presentation.theme.TienditaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val _productsViewModel: ProductsViewModel by viewModels()
    private lateinit var _navHostController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TienditaTheme {
                _navHostController = rememberNavController()
                NavHost(
                    navController = _navHostController,
                    startDestination = Routes.ProductsScreen.route
                ) {
                    composable(Routes.ProductsScreen.route) {
                        ProductsScreen()
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun MainPreview() {
        ProductsScreen()
    }

}