package com.ardondev.tiendita.presentation

sealed class Routes(
    val route: String
) {

    object ProductsScreen: Routes(route = "products")
    object AddProductScreen: Routes(route = "add_product")

}