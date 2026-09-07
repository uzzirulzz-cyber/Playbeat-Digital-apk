package com.example.model

import com.example.network.ApiProduct
import java.util.UUID

data class ProductPlan(
    val id: String,
    val name: String, // e.g. "1 Month", "3 Months", "6 Months", "1 Year" or "$25 Card"
    val price: Double,
    val originalPrice: Double? = null,
    val durationMonths: Int = 1,
    val isPopular: Boolean = false
)

data class ProductReview(
    val id: String,
    val authorName: String,
    val rating: Float,
    val date: String,
    val comment: String
)

enum class ProductCategory(val displayName: String, val iconName: String) {
    ALL("All Products", "apps"),
    PROJECTORS("Smart Projectors", "videocam"),
    SUBSCRIPTIONS("Subscriptions", "card_membership"),
    SOFTWARE("Software", "terminal"),
    STREAMING("Streaming", "live_tv"),
    GIFT_CARDS("Gift Cards", "card_giftcard"),
    GAMING("Gaming", "sports_esports"),
    SOCIAL_MEDIA("Social Media", "share"),
    WEB_HOSTING("Web Hosting", "dns"),
    DIGITAL_MARKETING("Marketing", "campaign"),
    WEB3("Web3", "currency_bitcoin"),
    SERVICES("Services", "miscellaneous_services");

    companion object {
        fun fromServerName(name: String?): ProductCategory {
            if (name.isNullOrBlank()) return ALL
            val clean = name.trim().lowercase()
            return when {
                clean.contains("projector") -> PROJECTORS
                clean.contains("subscri") -> SUBSCRIPTIONS
                clean.contains("software") -> SOFTWARE
                clean.contains("stream") -> STREAMING
                clean.contains("gift") -> GIFT_CARDS
                clean.contains("game") || clean.contains("gaming") -> GAMING
                clean.contains("social") -> SOCIAL_MEDIA
                clean.contains("host") -> WEB_HOSTING
                clean.contains("market") -> DIGITAL_MARKETING
                clean.contains("web3") || clean.contains("crypto") -> WEB3
                clean.contains("service") -> SERVICES
                else -> ALL
            }
        }
    }
}

data class Product(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val shortDescription: String,
    val fullDescription: String,
    val basePrice: Double,
    val originalPrice: Double? = null,
    val currency: String = "PKR",
    val rating: Float = 4.9f,
    val reviewCount: Int = 84,
    val inStock: Boolean = true,
    val instantDelivery: Boolean = true,
    val isTrending: Boolean = false,
    val isBestSeller: Boolean = false,
    val isNewArrival: Boolean = false,
    val isSpecialOffer: Boolean = false,
    val plans: List<ProductPlan> = emptyList(),
    val specifications: Map<String, String> = emptyMap(),
    val reviews: List<ProductReview> = emptyList(),
    val imageUrl: String = "",
    val sku: String = "",
    val slug: String = "",
    val productType: String = "digital",
    val discountPercent: Int = 0,
    val active: Boolean = true,
    val deliveryType: String = "Instant Auto-Email",
    val deliveryInfo: String = "Instant 15-Second Key Delivery",
    val region: String = "Global",
    val features: List<String> = emptyList()
)

fun ApiProduct.toDomainProduct(baseUrl: String = "https://playbeat.digital"): Product {
    val cleanImage = when {
        image.isNullOrBlank() -> ""
        image.startsWith("http://") || image.startsWith("https://") -> image
        image.startsWith("/") -> baseUrl.trimEnd('/') + image
        else -> "${baseUrl.trimEnd('/')}/$image"
    }

    val domainCategory = ProductCategory.fromServerName(category)
    val pId = mongoId ?: id ?: sku ?: slug ?: UUID.randomUUID().toString()
    val isDigital = digital != false && productType != "physical"

    val primaryPrice = price
    val originalP = originalPrice ?: compareAtPrice

    val planList = if (gallery != null && gallery.isNotEmpty()) {
        listOf(
            ProductPlan(
                id = "${pId}_std",
                name = if (isDigital) "Instant Digital License" else "Standard Hardware Unit",
                price = primaryPrice,
                originalPrice = originalP,
                isPopular = true
            )
        )
    } else {
        listOf(
            ProductPlan(
                id = "${pId}_std",
                name = if (isDigital) "1 License Key (Instant Delivery)" else "Standard Hardware Box",
                price = primaryPrice,
                originalPrice = originalP,
                isPopular = true
            )
        )
    }

    val specs = mutableMapOf<String, String>()
    if (!sku.isNullOrBlank()) specs["SKU"] = sku
    if (!region.isNullOrBlank()) specs["Region"] = region
    if (!deliveryType.isNullOrBlank()) specs["Delivery"] = deliveryType
    if (features != null && features.isNotEmpty()) {
        specs["Highlights"] = features.joinToString(", ")
    }

    return Product(
        id = pId,
        name = name.ifBlank { "PlayBeat Product" },
        category = domainCategory,
        shortDescription = shortDescription?.ifBlank { null }
            ?: description?.take(130)
            ?: "100% verified PlayBeat digital item with automated fast dispatch.",
        fullDescription = detailedDescription?.ifBlank { null }
            ?: description?.ifBlank { null }
            ?: "Official PlayBeat catalog item. Guaranteed authentic with instant auto-delivery and 24/7 dedicated support.",
        basePrice = primaryPrice,
        originalPrice = originalP,
        currency = currency ?: "PKR",
        rating = rating ?: 4.9f,
        reviewCount = reviewCount ?: 35,
        inStock = (stock ?: 1) > 0 && active != false,
        instantDelivery = isDigital,
        isTrending = isHot == true,
        isBestSeller = isFeatured == true || featured == true,
        isSpecialOffer = originalP != null && originalP > primaryPrice,
        plans = planList,
        specifications = specs,
        imageUrl = cleanImage,
        sku = sku ?: "",
        slug = slug ?: "",
        productType = productType ?: if (isDigital) "digital" else "physical",
        discountPercent = discountPercent ?: 0,
        active = active != false,
        deliveryType = deliveryType ?: if (isDigital) "Instant Auto-Email" else "Courier Express",
        deliveryInfo = deliveryInfo ?: if (isDigital) "Instant 15-Second Key Delivery" else "Fast trackable delivery",
        region = region ?: "Global",
        features = features ?: emptyList()
    )
}
