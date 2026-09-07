package com.example.model

enum class PaymentMethod(val title: String, val subtitle: String, val icon: String) {
    CREDIT_CARD("Credit / Debit Card", "Visa, MasterCard, 256-bit SSL", "credit_card"),
    JAZZCASH("JazzCash / EasyPaisa", "Instant mobile wallet payment", "account_balance_wallet"),
    BANK_TRANSFER("Bank Alfalah / Direct", "Online bank account transfer", "account_balance"),
    CRYPTO("Crypto (USDT / BTC)", "TRC20, ERC20 instant clearance", "currency_bitcoin"),
    PLAYBEAT_WALLET("Playbeat Store Credits", "Instant 1-tap checkout balance", "payments")
}

enum class OrderStatus(val label: String) {
    PAID("Paid"),
    PROCESSING("Processing"),
    FULFILLED("Delivered"),
    COMPLETED("Completed")
}

data class DigitalLicense(
    val licenseKey: String,
    val activationGuide: String,
    val downloadUrl: String? = null
)

data class OrderItem(
    val productName: String,
    val planName: String,
    val quantity: Int,
    val unitPrice: Double,
    val digitalLicense: DigitalLicense
)

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val discount: Double,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus = OrderStatus.FULFILLED,
    val customerEmail: String
)
