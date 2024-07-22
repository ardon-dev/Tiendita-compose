package com.ardondev.tiendita.presentation

sealed class Routes(
    val route: String
) {

    object ProductsScreen: Routes(route = "products")
    object AddProductScreen: Routes(route = "add_product")
    object ProductDetailScreen: Routes(route = "product_detail/{product_id}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }

}