package com.example.model

data class UserProfile(
    val id: String = "usr_9981",
    val name: String = "Alex Vance",
    val email: String = "alex.vance@playbeat.digital",
    val phone: String = "+1 (555) 019-2834",
    val isAuthenticated: Boolean = true,
    val walletBalance: Double = 45.00
)

data class NotificationPreferences(
    val orderUpdates: Boolean = true,
    val digitalDelivery: Boolean = true,
    val promoDiscounts: Boolean = false,
    val newArrivals: Boolean = true
)
