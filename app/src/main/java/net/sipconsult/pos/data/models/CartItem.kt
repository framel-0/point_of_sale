package net.sipconsult.pos.data.models

data class CartItem(var product: ProductItem, var quantity: Int = 1) {

    fun getProductPriceString(): String? = String.format("Ghc %.2f", product.price.salePrice)

    fun getProductQuantityString(): String? = String.format("%d", quantity)

}