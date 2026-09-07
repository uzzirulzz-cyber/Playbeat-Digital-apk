package com.example.model

data class CartItem(
    val product: Product,
    val selectedPlan: ProductPlan,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = selectedPlan.price * quantity
}
