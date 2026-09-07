package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiProduct(
    @Json(name = "_id") val mongoId: String? = null,
    val id: String? = null,
    val sku: String? = null,
    val name: String = "",
    val slug: String? = null,
    val category: String? = null,
    val productType: String? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val detailedDescription: String? = null,
    val price: Double = 0.0,
    val originalPrice: Double? = null,
    val compareAtPrice: Double? = null,
    val currency: String? = "PKR",
    val discountPercent: Int? = null,
    val image: String? = null,
    val gallery: List<String>? = null,
    val tags: List<String>? = null,
    val digital: Boolean? = true,
    val stock: Int? = 50,
    val status: String? = "active",
    val rating: Float? = 4.9f,
    val reviewCount: Int? = 10,
    val isHot: Boolean? = false,
    val isFeatured: Boolean? = false,
    val featured: Boolean? = false,
    val active: Boolean? = true,
    val deliveryType: String? = "Instant Auto-Email",
    val deliveryInfo: String? = null,
    val region: String? = "Global",
    val features: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class ApiProductListResponse(
    val success: Boolean = false,
    val count: Int? = 0,
    val total: Int? = 0,
    val page: Int? = 1,
    val totalPages: Int? = 1,
    val products: List<ApiProduct> = emptyList(),
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiSingleProductResponse(
    val success: Boolean = false,
    val message: String? = null,
    val product: ApiProduct? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiCategoryItem(
    val name: String,
    val slug: String? = null,
    val count: Int? = 0
)

@JsonClass(generateAdapter = true)
data class ApiCategoriesResponse(
    val success: Boolean = false,
    val categories: List<ApiCategoryItem> = emptyList(),
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiAdminLoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class ApiAdminUser(
    val email: String? = null,
    val name: String? = null,
    val role: String? = "admin"
)

@JsonClass(generateAdapter = true)
data class ApiAdminLoginResponse(
    val success: Boolean = false,
    val token: String? = null,
    val admin: ApiAdminUser? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiProductPayload(
    val name: String,
    val slug: String? = null,
    val sku: String? = null,
    val category: String,
    val productType: String = "digital",
    val description: String,
    val shortDescription: String? = null,
    val detailedDescription: String? = null,
    val price: Double,
    val originalPrice: Double? = null,
    val compareAtPrice: Double? = null,
    val currency: String = "PKR",
    val discountPercent: Int = 0,
    val image: String = "/playbeat-logo.png",
    val tags: List<String> = listOf("Verified", "Digital"),
    val digital: Boolean = true,
    val stock: Int = 50,
    val status: String = "in_stock",
    val rating: Float = 4.9f,
    val reviewCount: Int = 10,
    val isHot: Boolean = false,
    val isFeatured: Boolean = false,
    val featured: Boolean = false,
    val active: Boolean = true,
    val deliveryType: String = "Instant Auto-Email",
    val deliveryInfo: String = "Instant 15-Second Key Delivery",
    val region: String = "Global",
    val features: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ApiGenericResponse(
    val success: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
