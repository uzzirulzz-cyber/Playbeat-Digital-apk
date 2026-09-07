package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SyncState
import com.example.model.ProductCategory
import com.example.ui.components.PlaybeatTopBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminConsoleScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.NavyBlack
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.PlaybeatBlue
import com.example.ui.theme.PlaybeatOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppTab
import com.example.viewmodel.PlaybeatViewModel
import kotlinx.coroutines.launch

@Composable
fun PlaybeatApp(
    viewModel: PlaybeatViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentTab by viewModel.currentTab.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val selectedPlan by viewModel.selectedPlan.collectAsState()
    val selectedOrder by viewModel.selectedOrder.collectAsState()
    val isCartOpen by viewModel.isCartOpen.collectAsState()
    val isCheckoutOpen by viewModel.isCheckoutOpen.collectAsState()

    val products by viewModel.products.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val cartTotals by viewModel.cartTotals.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()

    // Live backend & admin states
    val syncState by viewModel.syncState.collectAsState()
    val adminToken by viewModel.adminToken.collectAsState()
    val adminUser by viewModel.adminUser.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()

    var isWishlistOpen by remember { mutableStateOf(false) }
    var isAdminOpen by remember { mutableStateOf(false) }

    val totalCartCount = cart.sumOf { it.quantity }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("playbeat_root_scaffold"),
        containerColor = NavyBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Show main top bar if not inside full detail, checkout, or admin flows
            if (selectedProduct == null && !isCartOpen && !isCheckoutOpen && !isWishlistOpen && !isAdminOpen) {
                PlaybeatTopBar(
                    cartCount = totalCartCount,
                    onSearchClick = {
                        viewModel.setTab(AppTab.SEARCH)
                    },
                    onCartClick = {
                        viewModel.openCart()
                    },
                    isLiveConnected = syncState is SyncState.Connected,
                    onAdminClick = {
                        isAdminOpen = true
                    }
                )
            }
        },
        bottomBar = {
            // Show bottom bar when on main tabs and not in modal flow
            if (selectedProduct == null && !isCartOpen && !isCheckoutOpen && !isWishlistOpen && !isAdminOpen) {
                PlaybeatBottomNavigation(
                    currentTab = currentTab,
                    orderCount = orders.size,
                    onTabSelected = { tab ->
                        viewModel.setTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NavyBlack)
        ) {
            when {
                // Admin Console flow
                isAdminOpen -> {
                    AdminConsoleScreen(
                        products = products,
                        syncState = syncState,
                        adminToken = adminToken,
                        adminUser = adminUser,
                        serverUrl = serverUrl,
                        activityLogs = activityLogs,
                        onBackClick = { isAdminOpen = false },
                        onRefreshCatalog = { viewModel.refreshCatalog() },
                        onAdminLogin = { email, pass, cb -> viewModel.adminLogin(email, pass, cb) },
                        onSetAdminToken = { token -> viewModel.setAdminToken(token) },
                        onAdminLogout = { viewModel.adminLogout() },
                        onPostProduct = { payload, cb -> viewModel.postNewProduct(payload, cb) },
                        onUpdateProduct = { id, payload, cb -> viewModel.updateProductContentAndPrice(id, payload, cb) },
                        onQuickUpdatePrice = { id, p, op, cb -> viewModel.quickUpdatePrice(id, p, op, cb) },
                        onDeleteProduct = { id, cb -> viewModel.deleteProduct(id, cb) },
                        onUpdateServerUrl = { url -> viewModel.setServerBaseUrl(url) }
                    )
                }
                // Checkout flow
                isCheckoutOpen -> {
                    CheckoutScreen(
                        cartItems = cart,
                        subtotal = cartTotals.subtotal,
                        discount = cartTotals.discount,
                        total = cartTotals.total,
                        initialEmail = userProfile.email,
                        onBackClick = { viewModel.closeCheckout() },
                        onOrderPlaced = { paymentMethod, email ->
                            viewModel.placeOrder(paymentMethod, email)
                            Toast.makeText(
                                context,
                                "Order confirmed! Digital keys dispatched to $email",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }

                // Cart view
                isCartOpen -> {
                    CartScreen(
                        cartItems = cart,
                        subtotal = cartTotals.subtotal,
                        discount = cartTotals.discount,
                        total = cartTotals.total,
                        appliedCoupon = cartTotals.couponCode,
                        onQuantityChange = { item, qty ->
                            viewModel.updateCartQuantity(item, qty)
                        },
                        onRemoveItem = { item ->
                            viewModel.removeFromCart(item)
                        },
                        onApplyCoupon = { code ->
                            val success = viewModel.applyCoupon(code)
                            if (success) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Coupon $code applied!")
                                }
                            }
                            success
                        },
                        onRemoveCoupon = {
                            viewModel.removeCoupon()
                        },
                        onProceedToCheckout = {
                            viewModel.openCheckout()
                        },
                        onContinueShopping = {
                            viewModel.closeCart()
                        }
                    )
                }

                // Wishlist view
                isWishlistOpen -> {
                    val wishlistedProducts = products.filter { wishlist.contains(it.id) }
                    WishlistScreen(
                        wishlistedProducts = wishlistedProducts,
                        onProductClick = { product ->
                            isWishlistOpen = false
                            viewModel.openProductDetails(product)
                        },
                        onAddToCart = { product ->
                            viewModel.addToCart(product)
                            Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                        },
                        onRemoveFromWishlist = { productId ->
                            viewModel.toggleWishlist(productId)
                        },
                        onBackClick = { isWishlistOpen = false }
                    )
                }

                // Product Detail view
                selectedProduct != null -> {
                    ProductDetailScreen(
                        product = selectedProduct!!,
                        selectedPlan = selectedPlan,
                        isWishlisted = wishlist.contains(selectedProduct!!.id),
                        allProducts = products,
                        wishlist = wishlist,
                        onPlanSelect = { plan -> viewModel.selectPlan(plan) },
                        onAddToCart = { qty ->
                            viewModel.addToCartCurrent(qty)
                            Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                        },
                        onBuyNow = { qty ->
                            viewModel.addToCartCurrent(qty)
                            viewModel.closeProductDetails()
                            viewModel.openCheckout()
                        },
                        onWishlistToggle = {
                            viewModel.toggleWishlist(selectedProduct!!.id)
                        },
                        onBackClick = { viewModel.closeProductDetails() },
                        onRelatedProductClick = { related ->
                            viewModel.openProductDetails(related)
                        },
                        onRelatedAddToCart = { related ->
                            viewModel.addToCart(related)
                            Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Main Tab navigation
                else -> {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            AppTab.HOME -> {
                                HomeScreen(
                                    products = products,
                                    wishlist = wishlist,
                                    selectedCategory = selectedCategory,
                                    onCategorySelect = { cat ->
                                        viewModel.setCategoryFilter(cat)
                                        viewModel.setTab(AppTab.SEARCH)
                                    },
                                    onProductClick = { product ->
                                        viewModel.openProductDetails(product)
                                    },
                                    onAddToCart = { product ->
                                        viewModel.addToCart(product)
                                        Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                                    },
                                    onWishlistToggle = { productId ->
                                        viewModel.toggleWishlist(productId)
                                    },
                                    onSearchClick = {
                                        viewModel.setTab(AppTab.SEARCH)
                                    },
                                    onExploreDealsClick = {
                                        viewModel.setCategoryFilter(ProductCategory.ALL)
                                        viewModel.setTab(AppTab.SEARCH)
                                    }
                                )
                            }

                            AppTab.CATEGORIES -> {
                                CategoriesScreen(
                                    allProducts = products,
                                    onCategoryClick = { cat ->
                                        viewModel.setCategoryFilter(cat)
                                        viewModel.setTab(AppTab.SEARCH)
                                    }
                                )
                            }

                            AppTab.SEARCH -> {
                                SearchScreen(
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                                    selectedCategory = selectedCategory,
                                    onCategorySelect = { cat -> viewModel.setCategoryFilter(cat) },
                                    products = filteredProducts,
                                    wishlist = wishlist,
                                    onProductClick = { product -> viewModel.openProductDetails(product) },
                                    onAddToCart = { product ->
                                        viewModel.addToCart(product)
                                        Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                                    },
                                    onWishlistToggle = { id -> viewModel.toggleWishlist(id) }
                                )
                            }

                            AppTab.ORDERS -> {
                                OrdersScreen(
                                    orders = orders,
                                    selectedOrder = selectedOrder,
                                    onSelectOrder = { order -> viewModel.openOrderDetails(order) },
                                    onCloseOrderDetails = { viewModel.closeOrderDetails() }
                                )
                            }

                            AppTab.ACCOUNT -> {
                                AccountScreen(
                                    userProfile = userProfile,
                                    orderCount = orders.size,
                                    wishlistCount = wishlist.size,
                                    notifications = notifications,
                                    onNotificationUpdate = { prefs ->
                                        viewModel.updateNotificationPreferences(prefs)
                                    },
                                    onViewOrdersClick = { viewModel.setTab(AppTab.ORDERS) },
                                    onViewWishlistClick = { isWishlistOpen = true },
                                    onLoginClick = { email, name -> viewModel.login(email, name) },
                                    onLogoutClick = { viewModel.logout() },
                                    onOpenAdminConsole = { isAdminOpen = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaybeatBottomNavigation(
    currentTab: AppTab,
    orderCount: Int,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = NavyCardBorder, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .testTag("playbeat_bottom_nav"),
        containerColor = NavyBlack,
        tonalElevation = 0.dp
    ) {
        val navItems = listOf(
            Triple(AppTab.HOME, "Home", Icons.Default.Home),
            Triple(AppTab.CATEGORIES, "Categories", Icons.Default.Apps),
            Triple(AppTab.SEARCH, "Search", Icons.Default.Search),
            Triple(AppTab.ORDERS, "Orders", Icons.Default.ReceiptLong),
            Triple(AppTab.ACCOUNT, "Profile", Icons.Default.Person)
        )

        navItems.forEach { (tab, title, icon) ->
            val isSelected = currentTab == tab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == AppTab.ORDERS && orderCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = PlaybeatOrange,
                                    contentColor = NavyBlack
                                ) {
                                    Text(
                                        text = orderCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        ) {
                            Icon(imageVector = icon, contentDescription = title)
                        }
                    } else {
                        Icon(imageVector = icon, contentDescription = title)
                    }
                },
                label = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PlaybeatBlue,
                    selectedTextColor = PlaybeatBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = PlaybeatBlue.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
