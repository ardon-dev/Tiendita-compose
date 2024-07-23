package com.ardondev.tiendita.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ardondev.tiendita.presentation.screens.product_detail.ProductDetailScreen
import com.ardondev.tiendita.presentation.screens.products.ProductsScreen
import com.ardondev.tiendita.presentation.theme.TienditaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TienditaTheme {

                val navHostController = rememberNavController()

                NavHost(
                    navController = navHostController,
                    startDestination = Routes.ProductsScreen.route
                ) {

                    //Products
                    composable(Routes.ProductsScreen.route) {
                        ProductsScreen(
                            navHostController = navHostController
                        )
                    }

                    //Product Detail
                    composable(
                        route = Routes.ProductDetailScreen.route,
                        arguments = listOf(
                            navArgument("product_id") {
                                type = NavType.LongType
                            }
                        )
                    ) { navBackStackEntry ->
                        val productId = navBackStackEntry.arguments?.getLong("product_id")
                        productId?.let {
                            ProductDetailScreen(
                                productId = productId,
                                navHostController = navHostController
                            )
                        }
                    }

                }
            }
        }
    }

}