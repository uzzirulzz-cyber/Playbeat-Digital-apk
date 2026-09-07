package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PlaybeatRepository
import com.example.data.SyncState
import com.example.model.CartItem
import com.example.model.NotificationPreferences
import com.example.model.Order
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductPlan
import com.example.model.UserProfile
import com.example.network.ApiAdminUser
import com.example.network.ApiProductPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    HOME("Home"),
    CATEGORIES("Categories"),
    SEARCH("Search"),
    ORDERS("Orders"),
    ACCOUNT("Account")
}

data class CartTotals(
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val couponCode: String? = null
)

class PlaybeatViewModel(
    private val repository: PlaybeatRepository = PlaybeatRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Nav stack helpers for in-app views
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedPlan = MutableStateFlow<ProductPlan?>(null)
    val selectedPlan: StateFlow<ProductPlan?> = _selectedPlan.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    private val _isCartOpen = MutableStateFlow(false)
    val isCartOpen: StateFlow<Boolean> = _isCartOpen.asStateFlow()

    private val _isCheckoutOpen = MutableStateFlow(false)
    val isCheckoutOpen: StateFlow<Boolean> = _isCheckoutOpen.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    // Search & Filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow(ProductCategory.ALL)
    val selectedCategoryFilter: StateFlow<ProductCategory> = _selectedCategoryFilter.asStateFlow()

    // Data streams from repository
    val products = repository.products
    val cart = repository.cart
    val wishlist = repository.wishlist
    val orders = repository.orders
    val userProfile = repository.userProfile
    val notifications = repository.notifications
    val appliedCoupon = repository.appliedCoupon

    // Live Server & Admin Streams
    val syncState = repository.syncState
    val adminToken = repository.adminToken
    val adminUser = repository.adminUser
    val serverUrl = repository.serverUrl
    val activityLogs = repository.activityLogs

    // Filtered products flow
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        _searchQuery,
        _selectedCategoryFilter
    ) { allProducts, query, category ->
        allProducts.filter { product ->
            val matchesCategory = (category == ProductCategory.ALL || product.category == category)
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.shortDescription.contains(query, ignoreCase = true) ||
                    product.category.displayName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Cart totals calculation
    val cartTotals: StateFlow<CartTotals> = combine(
        cart,
        appliedCoupon
    ) { cartItems, coupon ->
        val subtotal = cartItems.sumOf { it.totalPrice }
        val discount = when (coupon) {
            "PLAYBEAT10" -> subtotal * 0.10
            "WELCOME20" -> if (subtotal >= 20.0) 20.0 else subtotal
            else -> 0.0
        }
        val total = (subtotal - discount).coerceAtLeast(0.0)
        CartTotals(
            subtotal = subtotal,
            discount = discount,
            total = total,
            couponCode = coupon
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, CartTotals())

    // UI actions
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
        _selectedProduct.value = null
        _selectedOrder.value = null
    }

    fun openProductDetails(product: Product) {
        _selectedProduct.value = product
        _selectedPlan.value = product.plans.firstOrNull() ?: ProductPlan(
            id = "default",
            name = "Standard License",
            price = product.basePrice
        )
    }

    fun selectPlan(plan: ProductPlan) {
        _selectedPlan.value = plan
    }

    fun closeProductDetails() {
        _selectedProduct.value = null
        _selectedPlan.value = null
    }

    fun openOrderDetails(order: Order) {
        _selectedOrder.value = order
    }

    fun closeOrderDetails() {
        _selectedOrder.value = null
    }

    fun openCart() {
        _isCartOpen.value = true
    }

    fun closeCart() {
        _isCartOpen.value = false
    }

    fun openCheckout() {
        _isCartOpen.value = false
        _isCheckoutOpen.value = true
    }

    fun closeCheckout() {
        _isCheckoutOpen.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: ProductCategory) {
        _selectedCategoryFilter.value = category
    }

    fun addToCartCurrent(quantity: Int = 1) {
        val product = _selectedProduct.value ?: return
        val plan = _selectedPlan.value ?: return
        repository.addToCart(product, plan, quantity)
    }

    fun addToCart(product: Product, plan: ProductPlan? = null) {
        val p = plan ?: product.plans.firstOrNull() ?: ProductPlan(
            id = "std",
            name = "Standard",
            price = product.basePrice
        )
        repository.addToCart(product, p, 1)
    }

    fun updateCartQuantity(item: CartItem, newQty: Int) {
        repository.updateCartQuantity(item, newQty)
    }

    fun removeFromCart(item: CartItem) {
        repository.removeFromCart(item)
    }

    fun toggleWishlist(productId: String) {
        repository.toggleWishlist(productId)
    }

    fun applyCoupon(code: String): Boolean {
        return repository.applyCoupon(code)
    }

    fun removeCoupon() {
        repository.removeCoupon()
    }

    fun placeOrder(paymentMethod: PaymentMethod, customerEmail: String) {
        val totals = cartTotals.value
        val order = repository.placeOrder(
            paymentMethod = paymentMethod,
            customerEmail = customerEmail,
            subtotal = totals.subtotal,
            discount = totals.discount,
            total = totals.total
        )
        _lastPlacedOrder.value = order
        _isCheckoutOpen.value = false
        _selectedOrder.value = order
        _currentTab.value = AppTab.ORDERS
    }

    fun dismissLastPlacedOrder() {
        _lastPlacedOrder.value = null
    }

    fun updateNotificationPreferences(prefs: NotificationPreferences) {
        repository.updateNotificationPreferences(prefs)
    }

    fun login(email: String, name: String) {
        repository.login(email, name)
    }

    fun logout() {
        repository.logout()
    }

    // Live Server & Admin Operations
    fun refreshCatalog(onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.refreshLiveProducts()
            onComplete?.invoke(result.isSuccess)
        }
    }

    fun adminLogin(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.adminLogin(email, pass)
            if (res.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, res.exceptionOrNull()?.message)
            }
        }
    }

    fun setAdminToken(token: String) {
        repository.setAdminToken(token)
    }

    fun adminLogout() {
        repository.adminLogout()
    }

    fun postNewProduct(payload: ApiProductPayload, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.postNewProduct(payload)
            if (res.isSuccess) {
                onResult(true, "Successfully published to live catalog!")
            } else {
                onResult(false, res.exceptionOrNull()?.message)
            }
        }
    }

    fun updateProductContentAndPrice(id: String, payload: ApiProductPayload, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.updateProductContentAndPrice(id, payload)
            if (res.isSuccess) {
                onResult(true, "Product & prices updated successfully!")
            } else {
                onResult(false, res.exceptionOrNull()?.message)
            }
        }
    }

    fun quickUpdatePrice(productId: String, newPrice: Double, newOriginalPrice: Double?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.quickUpdatePrice(productId, newPrice, newOriginalPrice)
            if (res.isSuccess) {
                onResult(true, "Price updated on catalog!")
            } else {
                onResult(false, res.exceptionOrNull()?.message)
            }
        }
    }

    fun deleteProduct(productId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.deleteProduct(productId)
            if (res.isSuccess) {
                onResult(true, "Product removed from catalog")
            } else {
                onResult(false, res.exceptionOrNull()?.message)
            }
        }
    }

    fun setServerBaseUrl(url: String) {
        repository.setServerBaseUrl(url)
    }
}
