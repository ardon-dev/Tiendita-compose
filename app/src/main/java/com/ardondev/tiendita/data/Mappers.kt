package com.ardondev.tiendita.data

import com.ardondev.tiendita.data.source.local.entity.ProductEntity
import com.ardondev.tiendita.domain.model.Product

/** Product **/

fun ProductEntity.toModel(): Product {
    return Product(id, name, stock, price)
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(id, name, stock, price)
}