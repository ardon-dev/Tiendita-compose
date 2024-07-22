package com.ardondev.tiendita.data

import com.ardondev.tiendita.data.source.local.entity.ProductEntity
import com.ardondev.tiendita.data.source.local.entity.SaleEntity
import com.ardondev.tiendita.domain.model.Product
import com.ardondev.tiendita.domain.model.Sale

/** Product **/

fun ProductEntity.toModel(): Product {
    return Product(id, name, stock, price)
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(id, name, stock, price)
}

/** Sale **/

fun SaleEntity.toModel(): Sale {
    return Sale(id, amount, quantity, total, productId)
}

fun Sale.toEntity(): SaleEntity {
    return SaleEntity(id, amount, quantity, total, productId)
}