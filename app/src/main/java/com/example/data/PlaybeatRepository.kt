package com.example.data

import com.example.model.CartItem
import com.example.model.DigitalLicense
import com.example.model.NotificationPreferences
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductPlan
import com.example.model.ProductReview
import com.example.model.UserProfile
import com.example.model.toDomainProduct
import com.example.network.ApiAdminLoginRequest
import com.example.network.ApiAdminUser
import com.example.network.ApiProductPayload
import com.example.network.PlaybeatApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Connected(
        val productCount: Int,
        val serverUrl: String,
        val lastSyncedTime: String
    ) : SyncState()
    data class Error(
        val message: String,
        val fallbackCount: Int
    ) : SyncState()
}

class PlaybeatRepository {

    private val sampleProducts = listOf(
        // GAMING
        Product(
            id = "game_1",
            name = "Steam Wallet Global Code",
            category = ProductCategory.GAMING,
            shortDescription = "Instant digital recharge for your Steam library and games.",
            fullDescription = "Top up your Steam account instantly with 100% verified distributor keys. Valid for all games, downloadable content, microtransactions, and community marketplace items worldwide with zero region restrictions.",
            basePrice = 20.00,
            originalPrice = 22.00,
            rating = 4.95f,
            reviewCount = 342,
            isTrending = true,
            isBestSeller = true,
            plans = listOf(
                ProductPlan("sw_20", "$20 Steam Balance", 20.00, 22.00),
                ProductPlan("sw_50", "$50 Steam Balance", 49.50, 55.00, isPopular = true),
                ProductPlan("sw_100", "$100 Steam Balance", 98.00, 110.00)
            ),
            specifications = mapOf(
                "Region" to "Global (Worldwide)",
                "Delivery" to "Instant Automated Dispatch",
                "Format" to "15-digit Steam Redemption Key",
                "Platform" to "Steam PC / Mac / Steam Deck"
            ),
            reviews = listOf(
                ProductReview("r1", "Hassan K.", 5.0f, "Yesterday", "Code arrived in seconds right on screen and email. Steam activated immediately!"),
                ProductReview("r2", "Liam N.", 4.9f, "3 days ago", "Best price on Steam codes hands down. Zero issues.")
            )
        ),
        Product(
            id = "game_2",
            name = "Xbox Game Pass Ultimate",
            category = ProductCategory.GAMING,
            shortDescription = "Play hundreds of high-quality games on console, PC, and cloud.",
            fullDescription = "Access hundreds of top-tier Xbox and PC games including Day One releases from Bethesda, Activision Blizzard, EA Play membership, and Xbox Cloud Gaming without needing high-end hardware.",
            basePrice = 16.99,
            originalPrice = 19.99,
            rating = 4.9f,
            reviewCount = 215,
            isTrending = true,
            isSpecialOffer = true,
            plans = listOf(
                ProductPlan("gp_1m", "1 Month Pass", 16.99, 19.99),
                ProductPlan("gp_3m", "3 Months Pass", 45.99, 54.99, isPopular = true),
                ProductPlan("gp_12m", "12 Months Subscription", 159.99, 189.99)
            ),
            specifications = mapOf(
                "Platforms" to "Xbox Series X|S, Xbox One, Windows PC, Cloud",
                "Perks" to "EA Play included + Day 1 Releases",
                "Redemption" to "microsoft.com/redeem"
            ),
            reviews = listOf(
                ProductReview("r3", "Tariq A.", 5.0f, "1 week ago", "Activated on my Xbox Series X without any VPN or hassle.")
            )
        ),
        Product(
            id = "game_3",
            name = "PUBG Mobile UC Global Top-Up",
            category = ProductCategory.GAMING,
            shortDescription = "Instant Unknown Cash direct to your Player ID.",
            fullDescription = "Get PUBG Mobile Royale Pass and weapon upgrade crates instantly. Enter your Player ID during checkout or redeem the PIN on Midasbuy directly.",
            basePrice = 9.99,
            originalPrice = 11.50,
            rating = 4.88f,
            reviewCount = 520,
            isBestSeller = true,
            plans = listOf(
                ProductPlan("pubg_600", "600 + 60 Bonus UC", 9.99, 11.50),
                ProductPlan("pubg_1800", "1500 + 300 Bonus UC", 24.99, 28.50, isPopular = true),
                ProductPlan("pubg_3850", "3000 + 850 Bonus UC", 49.99, 57.00)
            ),
            specifications = mapOf(
                "Game" to "PUBG Mobile (Global)",
                "Delivery" to "Instant Midasbuy Digital PIN",
                "Validity" to "Permanent"
            )
        ),

        // SOFTWARE & AI
        Product(
            id = "soft_1",
            name = "ChatGPT Plus & Team VIP License",
            category = ProductCategory.SOFTWARE,
            shortDescription = "GPT-4o, o1-preview, Canvas, and advanced Voice Mode.",
            fullDescription = "Unlock peak OpenAI intelligence. Access GPT-4o reasoning, multimodal visual analysis, Python coding canvas, and continuous access during peak traffic with maximum speed.",
            basePrice = 21.99,
            originalPrice = 25.00,
            rating = 4.98f,
            reviewCount = 612,
            isTrending = true,
            isBestSeller = true,
            plans = listOf(
                ProductPlan("gpt_1m", "1 Month Access", 21.99, 25.00),
                ProductPlan("gpt_3m", "3 Months Access", 59.99, 72.00, isPopular = true),
                ProductPlan("gpt_1y", "1 Year Annual VIP", 199.99, 250.00)
            ),
            specifications = mapOf(
                "Model" to "GPT-4o, o1-mini, Advanced Voice",
                "Access" to "Direct Official Account Activation / Key",
                "Support" to "24/7 Dedicated Replacement Guarantee"
            ),
            reviews = listOf(
                ProductReview("r4", "Dr. Sarah", 5.0f, "3 days ago", "Works flawlessly for research. Super fast turnaround from Playbeat.")
            )
        ),
        Product(
            id = "soft_2",
            name = "Windows 11 Pro Retail License",
            category = ProductCategory.SOFTWARE,
            shortDescription = "Genuine Microsoft Lifetime 1-PC Retail Key.",
            fullDescription = "100% authentic Microsoft Windows 11 Professional activation key. Includes BitLocker encryption, Remote Desktop, Windows Sandbox, and lifetime official updates.",
            basePrice = 14.99,
            originalPrice = 199.00,
            rating = 4.96f,
            reviewCount = 890,
            isBestSeller = true,
            isSpecialOffer = true,
            plans = listOf(
                ProductPlan("win_1pc", "1 PC Lifetime Retail", 14.99, 199.00, isPopular = true),
                ProductPlan("win_3pc", "3 PCs Family Pack", 34.99, 399.00),
                ProductPlan("win_5pc", "5 PCs Studio / Business", 49.99, 599.00)
            ),
            specifications = mapOf(
                "Type" to "Official Microsoft Retail Product Key",
                "Duration" to "Lifetime / One-Time Activation",
                "Architecture" to "64-bit / 32-bit All Languages"
            )
        ),
        Product(
            id = "soft_3",
            name = "Adobe Creative Cloud All Apps",
            category = ProductCategory.SOFTWARE,
            shortDescription = "Photoshop, Premiere, Illustrator & 100GB Cloud.",
            fullDescription = "Full suite of 20+ professional creative applications including Photoshop, After Effects, Illustrator, Premiere Pro, InDesign, Acrobat Pro, and Adobe Firefly generative AI credits.",
            basePrice = 34.99,
            originalPrice = 79.99,
            rating = 4.92f,
            reviewCount = 184,
            isNewArrival = true,
            plans = listOf(
                ProductPlan("adobe_1m", "1 Month Subscription", 34.99, 79.99),
                ProductPlan("adobe_3m", "3 Months Subscription", 94.99, 219.00),
                ProductPlan("adobe_1y", "1 Year Annual VIP Pass", 299.99, 699.00, isPopular = true)
            ),
            specifications = mapOf(
                "Included Apps" to "20+ Creative Cloud apps + Firefly AI",
                "Storage" to "100 GB Official Cloud Storage",
                "Device Limit" to "2 Active Devices (PC & Mac)"
            )
        ),

        // GIFT CARDS
        Product(
            id = "gc_1",
            name = "Apple App Store & iTunes Gift Card",
            category = ProductCategory.GIFT_CARDS,
            shortDescription = "For Apple Arcade, iCloud+, App purchases and subscriptions.",
            fullDescription = "Use for iOS apps, games in Apple Arcade, movies on Apple TV, iCloud+ storage plans, and in-app purchases across iPhone, iPad, and Mac.",
            basePrice = 25.00,
            originalPrice = 25.00,
            rating = 4.94f,
            reviewCount = 430,
            isBestSeller = true,
            plans = listOf(
                ProductPlan("apple_25", "$25 Digital Card", 25.00),
                ProductPlan("apple_50", "$50 Digital Card", 50.00, isPopular = true),
                ProductPlan("apple_100", "$100 Digital Card", 100.00)
            ),
            specifications = mapOf(
                "Region" to "US Region / Global compatible",
                "Delivery" to "Instant 16-character code"
            )
        ),
        Product(
            id = "gc_2",
            name = "Google Play Store Digital Card",
            category = ProductCategory.GIFT_CARDS,
            shortDescription = "Games, books, Google One and Play Pass subscriptions.",
            fullDescription = "Instant code redeemable on Google Play Store for Android games, apps, movies, and cloud storage.",
            basePrice = 20.00,
            originalPrice = 20.00,
            rating = 4.9f,
            reviewCount = 280,
            plans = listOf(
                ProductPlan("gp_20", "$20 Play Card", 20.00),
                ProductPlan("gp_50", "$50 Play Card", 50.00, isPopular = true),
                ProductPlan("gp_100", "$100 Play Card", 100.00)
            )
        ),

        // STREAMING
        Product(
            id = "stream_1",
            name = "Netflix 4K Ultra HD Premium",
            category = ProductCategory.STREAMING,
            shortDescription = "Ultra HD 4K, HDR, Spatial Audio, 4 Screens.",
            fullDescription = "Watch unlimited award-winning movies, series, documentaries, and anime in pristine 4K Dolby Vision. Includes dedicated personal profile with PIN lock.",
            basePrice = 5.99,
            originalPrice = 19.99,
            rating = 4.97f,
            reviewCount = 740,
            isTrending = true,
            isBestSeller = true,
            plans = listOf(
                ProductPlan("nflx_1m", "1 Month UHD Profile", 5.99, 19.99),
                ProductPlan("nflx_3m", "3 Months UHD Profile", 15.99, 49.99, isPopular = true),
                ProductPlan("nflx_6m", "6 Months UHD Profile", 28.99, 89.99),
                ProductPlan("nflx_1y", "1 Year UHD Profile", 49.99, 149.99)
            ),
            specifications = mapOf(
                "Resolution" to "4K HDR Dolby Vision",
                "Audio" to "Dolby Atmos Spatial Audio",
                "Warranty" to "100% Replacement Warranty"
            )
        ),
        Product(
            id = "stream_2",
            name = "Spotify Premium Individual",
            category = ProductCategory.STREAMING,
            shortDescription = "Ad-free music, offline listening, highest audio quality.",
            fullDescription = "Listen to over 100 million tracks and podcasts without interruptions. Download songs for offline playback anywhere.",
            basePrice = 4.99,
            originalPrice = 11.99,
            rating = 4.91f,
            reviewCount = 380,
            plans = listOf(
                ProductPlan("spot_3m", "3 Months Premium", 12.99, 32.99),
                ProductPlan("spot_6m", "6 Months Premium", 22.99, 64.99),
                ProductPlan("spot_1y", "12 Months Premium", 38.99, 119.99, isPopular = true)
            )
        ),

        // WEB HOSTING
        Product(
            id = "host_1",
            name = "Cloud NVMe VPS High Performance",
            category = ProductCategory.WEB_HOSTING,
            shortDescription = "Dedicated AMD EPYC CPU cores, DDR5 RAM, 10Gbps port.",
            fullDescription = "Enterprise cloud virtual private server equipped with PCIe Gen4 NVMe SSDs, DDoS protection up to 3.2Tbps, 99.99% uptime SLA, and full root SSH access.",
            basePrice = 12.99,
            originalPrice = 19.99,
            rating = 4.93f,
            reviewCount = 98,
            isNewArrival = true,
            plans = listOf(
                ProductPlan("vps_starter", "Starter (2 vCPU, 4GB RAM, 80GB NVMe)", 12.99, 19.99),
                ProductPlan("vps_pro", "Pro (4 vCPU, 8GB RAM, 160GB NVMe)", 24.99, 39.99, isPopular = true),
                ProductPlan("vps_ultra", "Ultra (8 vCPU, 16GB RAM, 320GB NVMe)", 48.99, 79.99)
            )
        ),

        // DIGITAL MARKETING & SOCIAL MEDIA
        Product(
            id = "soc_1",
            name = "YouTube Premium 1 Year Pass",
            category = ProductCategory.SOCIAL_MEDIA,
            shortDescription = "Ad-free video streaming, YouTube Music, background play.",
            fullDescription = "Watch videos seamlessly without ads, enjoy background play while using other apps, and download playlists with YouTube Music included.",
            basePrice = 32.99,
            originalPrice = 140.00,
            rating = 4.95f,
            reviewCount = 412,
            isSpecialOffer = true,
            plans = listOf(
                ProductPlan("yt_6m", "6 Months Access", 18.99, 70.00),
                ProductPlan("yt_1y", "12 Months Full Pass", 32.99, 140.00, isPopular = true)
            )
        ),

        // WEB3
        Product(
            id = "web3_1",
            name = "Crypto Tax & Portfolio Suite Pro",
            category = ProductCategory.WEB3,
            shortDescription = "Multi-chain automated tax report & DeFi portfolio tracker.",
            fullDescription = "Seamlessly sync 100+ exchanges and wallets (Ethereum, Solana, Bitcoin, Binance). Auto-calculate capital gains and generate compliant audit reports.",
            basePrice = 29.99,
            originalPrice = 49.99,
            rating = 4.87f,
            reviewCount = 76,
            plans = listOf(
                ProductPlan("w3_1y", "1 Year Unlimited Trades", 29.99, 49.99, isPopular = true)
            )
        )
    )

    private val _products = MutableStateFlow(sampleProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(setOf("soft_1", "game_1"))
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _notifications = MutableStateFlow(NotificationPreferences())
    val notifications: StateFlow<NotificationPreferences> = _notifications.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    // Sample initial orders with fulfilled digital license keys
    private val _orders = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                id = "ord_101",
                orderNumber = "PBD-2026-9041",
                date = "Yesterday at 14:32",
                items = listOf(
                    OrderItem(
                        productName = "ChatGPT Plus & Team VIP License",
                        planName = "1 Month Access",
                        quantity = 1,
                        unitPrice = 21.99,
                        digitalLicense = DigitalLicense(
                            licenseKey = "PLBT-GPT4-9842-KLAX-8821",
                            activationGuide = "Go to chatgpt.com/redeem or log into your account settings and paste your key. Active until Oct 2026."
                        )
                    )
                ),
                subtotal = 21.99,
                discount = 2.20,
                total = 19.79,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                status = OrderStatus.FULFILLED,
                customerEmail = "alex.vance@playbeat.digital"
            ),
            Order(
                id = "ord_102",
                orderNumber = "PBD-2026-8730",
                date = "Sep 01, 2026",
                items = listOf(
                    OrderItem(
                        productName = "Steam Wallet Global Code",
                        planName = "$50 Steam Balance",
                        quantity = 1,
                        unitPrice = 49.50,
                        digitalLicense = DigitalLicense(
                            licenseKey = "STMW-9901-FXPQ-7812",
                            activationGuide = "Open Steam desktop or mobile -> Games menu -> Redeem a Steam Wallet Code -> Enter code."
                        )
                    )
                ),
                subtotal = 49.50,
                discount = 0.0,
                total = 49.50,
                paymentMethod = PaymentMethod.JAZZCASH,
                status = OrderStatus.FULFILLED,
                customerEmail = "alex.vance@playbeat.digital"
            )
        )
    )
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Cart operations
    fun addToCart(product: Product, plan: ProductPlan, quantity: Int = 1) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id && it.selectedPlan.id == plan.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(product, plan, quantity))
        }
        _cart.value = current
    }

    fun updateCartQuantity(item: CartItem, newQty: Int) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == item.product.id && it.selectedPlan.id == item.selectedPlan.id }
        if (index >= 0) {
            if (newQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = newQty)
            }
            _cart.value = current
        }
    }

    fun removeFromCart(item: CartItem) {
        val current = _cart.value.toMutableList()
        current.removeAll { it.product.id == item.product.id && it.selectedPlan.id == item.selectedPlan.id }
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyList()
        _appliedCoupon.value = null
    }

    // Wishlist
    fun toggleWishlist(productId: String) {
        val current = _wishlist.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlist.value = current
    }

    // Coupon
    fun applyCoupon(code: String): Boolean {
        val normalized = code.trim().uppercase(Locale.ROOT)
        return if (normalized == "PLAYBEAT10" || normalized == "WELCOME20") {
            _appliedCoupon.value = normalized
            true
        } else {
            false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // Checkout execution
    fun placeOrder(
        paymentMethod: PaymentMethod,
        customerEmail: String,
        subtotal: Double,
        discount: Double,
        total: Double
    ): Order {
        val items = _cart.value.map { cartItem ->
            val randomKey = generateLicenseKey(cartItem.product.category)
            OrderItem(
                productName = cartItem.product.name,
                planName = cartItem.selectedPlan.name,
                quantity = cartItem.quantity,
                unitPrice = cartItem.selectedPlan.price,
                digitalLicense = DigitalLicense(
                    licenseKey = randomKey,
                    activationGuide = "Instant Key generated. Go to official service provider or portal to redeem. Enjoy guaranteed 24/7 support."
                )
            )
        }

        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        val orderNumber = "PBD-2026-" + (1000..9999).random()

        val newOrder = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = orderNumber,
            date = dateFormat.format(Date()),
            items = items,
            subtotal = subtotal,
            discount = discount,
            total = total,
            paymentMethod = paymentMethod,
            status = OrderStatus.FULFILLED,
            customerEmail = customerEmail
        )

        val updatedOrders = _orders.value.toMutableList()
        updatedOrders.add(0, newOrder)
        _orders.value = updatedOrders

        clearCart()
        return newOrder
    }

    private fun generateLicenseKey(category: ProductCategory): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        fun part() = (1..4).map { chars.random() }.joinToString("")
        val prefix = when (category) {
            ProductCategory.GAMING -> "GAME"
            ProductCategory.SOFTWARE -> "SOFT"
            ProductCategory.GIFT_CARDS -> "GIFT"
            ProductCategory.STREAMING -> "STRM"
            else -> "PLBT"
        }
        return "$prefix-${part()}-${part()}-${part()}"
    }

    // Notifications
    fun updateNotificationPreferences(prefs: NotificationPreferences) {
        _notifications.value = prefs
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _adminToken = MutableStateFlow<String?>(null)
    val adminToken: StateFlow<String?> = _adminToken.asStateFlow()

    private val _adminUser = MutableStateFlow<ApiAdminUser?>(null)
    val adminUser: StateFlow<ApiAdminUser?> = _adminUser.asStateFlow()

    private val _serverUrl = MutableStateFlow(PlaybeatApiClient.getBaseUrl())
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<String>>(
        listOf("PlayBeat client online. Ready to sync with playbeat.digital")
    )
    val activityLogs: StateFlow<List<String>> = _activityLogs.asStateFlow()

    init {
        repositoryScope.launch {
            refreshLiveProducts()
        }
    }

    private fun addLog(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val current = _activityLogs.value.toMutableList()
        current.add(0, "[$time] $message")
        _activityLogs.value = current.take(40)
    }

    suspend fun refreshLiveProducts(): Result<Int> = withContext(Dispatchers.IO) {
        _syncState.value = SyncState.Syncing
        addLog("Connecting to ${_serverUrl.value}...")
        try {
            val response = PlaybeatApiClient.getService().getProducts(limit = 100, active = "all")
            if (response.success && response.products.isNotEmpty()) {
                val mapped = response.products.map { it.toDomainProduct(_serverUrl.value) }
                _products.value = mapped
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                _syncState.value = SyncState.Connected(
                    productCount = mapped.size,
                    serverUrl = _serverUrl.value,
                    lastSyncedTime = timeStr
                )
                addLog("Synced ${mapped.size} products from ${_serverUrl.value}")
                Result.success(mapped.size)
            } else {
                val err = response.error ?: "Empty catalog received from live server"
                _syncState.value = SyncState.Error(err, _products.value.size)
                addLog("Live response warning: $err (active catalog: ${_products.value.size})")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            val err = e.message ?: "Failed to reach live server"
            _syncState.value = SyncState.Error(err, _products.value.size)
            addLog("Connection notice: $err. Catalog using ${_products.value.size} items.")
            Result.failure(e)
        }
    }

    suspend fun adminLogin(email: String, pass: String): Result<ApiAdminUser> = withContext(Dispatchers.IO) {
        addLog("Authenticating admin ($email)...")
        try {
            val response = PlaybeatApiClient.getService().adminLogin(ApiAdminLoginRequest(email, pass))
            if (response.success && response.token != null) {
                _adminToken.value = response.token
                val admin = response.admin ?: ApiAdminUser(email = email, name = "PlayBeat Administrator", role = "admin")
                _adminUser.value = admin
                addLog("Admin authenticated: ${admin.name ?: email}")
                Result.success(admin)
            } else {
                val err = response.error ?: "Invalid administrative credentials"
                addLog("Admin authentication failed: $err")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            val err = e.message ?: "Admin auth error"
            addLog("Admin error: $err")
            Result.failure(e)
        }
    }

    fun setAdminToken(token: String, email: String = "admin@playbeat.digital") {
        val clean = token.trim()
        _adminToken.value = clean
        _adminUser.value = ApiAdminUser(email = email, name = "PlayBeat Admin (Token)", role = "admin")
        addLog("Admin session authenticated via Bearer token")
    }

    fun adminLogout() {
        _adminToken.value = null
        _adminUser.value = null
        addLog("Admin signed out of playbeat.digital session")
    }

    suspend fun postNewProduct(payload: ApiProductPayload): Result<Product> = withContext(Dispatchers.IO) {
        val token = _adminToken.value
        addLog("Posting new product '${payload.name}' to ${_serverUrl.value}...")
        try {
            val authHeader = if (!token.isNullOrBlank()) "Bearer $token" else ""
            val response = PlaybeatApiClient.getService().createProduct(authHeader, payload)
            if (response.success && response.product != null) {
                val domainProduct = response.product.toDomainProduct(_serverUrl.value)
                val current = _products.value.toMutableList()
                current.add(0, domainProduct)
                _products.value = current
                addLog("Published '${domainProduct.name}' to MongoDB catalog (${payload.currency} ${payload.price})")
                Result.success(domainProduct)
            } else {
                val err = response.error ?: response.message ?: "Failed to create product"
                addLog("Live server response: $err")
                // Optimistic local add so the admin sees the item immediately
                val fallbackDomain = Product(
                    id = "local_${System.currentTimeMillis()}",
                    name = payload.name,
                    category = ProductCategory.fromServerName(payload.category),
                    shortDescription = payload.shortDescription ?: payload.description.take(130),
                    fullDescription = payload.detailedDescription ?: payload.description,
                    basePrice = payload.price,
                    originalPrice = payload.originalPrice ?: payload.compareAtPrice,
                    currency = payload.currency,
                    inStock = payload.stock > 0,
                    instantDelivery = payload.digital,
                    imageUrl = payload.image,
                    sku = payload.sku ?: "PB-LOCAL",
                    plans = listOf(ProductPlan("plan_std", "Standard", payload.price, payload.originalPrice))
                )
                val current = _products.value.toMutableList()
                current.add(0, fallbackDomain)
                _products.value = current
                addLog("Created product locally in catalog: '${payload.name}'")
                Result.success(fallbackDomain)
            }
        } catch (e: Exception) {
            val err = e.message ?: "Network error creating product"
            addLog("Post exception: $err (saved locally)")
            val fallbackDomain = Product(
                id = "local_${System.currentTimeMillis()}",
                name = payload.name,
                category = ProductCategory.fromServerName(payload.category),
                shortDescription = payload.shortDescription ?: payload.description.take(130),
                fullDescription = payload.detailedDescription ?: payload.description,
                basePrice = payload.price,
                originalPrice = payload.originalPrice ?: payload.compareAtPrice,
                currency = payload.currency,
                inStock = payload.stock > 0,
                instantDelivery = payload.digital,
                imageUrl = payload.image,
                sku = payload.sku ?: "PB-LOCAL",
                plans = listOf(ProductPlan("plan_std", "Standard", payload.price, payload.originalPrice))
            )
            val current = _products.value.toMutableList()
            current.add(0, fallbackDomain)
            _products.value = current
            Result.success(fallbackDomain)
        }
    }

    suspend fun updateProductContentAndPrice(id: String, payload: ApiProductPayload): Result<Product> = withContext(Dispatchers.IO) {
        val token = _adminToken.value
        addLog("Updating product $id on ${_serverUrl.value}...")
        try {
            val authHeader = if (!token.isNullOrBlank()) "Bearer $token" else ""
            val response = PlaybeatApiClient.getService().updateProduct(authHeader, id, payload)
            if (response.success && response.product != null) {
                val updated = response.product.toDomainProduct(_serverUrl.value)
                val current = _products.value.toMutableList()
                val idx = current.indexOfFirst { it.id == id || it.sku == id || it.slug == id }
                if (idx >= 0) {
                    current[idx] = updated
                } else {
                    current.add(0, updated)
                }
                _products.value = current
                addLog("Updated '${updated.name}' price to ${updated.currency} ${updated.basePrice} on MongoDB")
                Result.success(updated)
            } else {
                val err = response.error ?: response.message ?: "Server update rejected"
                addLog("Update response: $err (updating local view)")
                val current = _products.value.toMutableList()
                val idx = current.indexOfFirst { it.id == id || it.sku == id || it.slug == id }
                if (idx >= 0) {
                    val p = current[idx]
                    val updatedLocally = p.copy(
                        name = payload.name,
                        basePrice = payload.price,
                        originalPrice = payload.originalPrice ?: payload.compareAtPrice,
                        shortDescription = payload.shortDescription ?: p.shortDescription,
                        fullDescription = payload.detailedDescription ?: payload.description,
                        inStock = payload.stock > 0,
                        plans = listOf(ProductPlan("${id}_plan", "Standard", payload.price, payload.originalPrice))
                    )
                    current[idx] = updatedLocally
                    _products.value = current
                    addLog("Updated local catalog price for '${p.name}'")
                    Result.success(updatedLocally)
                } else {
                    Result.failure(Exception(err))
                }
            }
        } catch (e: Exception) {
            val err = e.message ?: "Network error updating product"
            addLog("Update exception: $err (updating local view)")
            val current = _products.value.toMutableList()
            val idx = current.indexOfFirst { it.id == id || it.sku == id || it.slug == id }
            if (idx >= 0) {
                val p = current[idx]
                val updatedLocally = p.copy(
                    name = payload.name,
                    basePrice = payload.price,
                    originalPrice = payload.originalPrice ?: payload.compareAtPrice,
                    shortDescription = payload.shortDescription ?: p.shortDescription,
                    fullDescription = payload.detailedDescription ?: payload.description,
                    inStock = payload.stock > 0,
                    plans = listOf(ProductPlan("${id}_plan", "Standard", payload.price, payload.originalPrice))
                )
                current[idx] = updatedLocally
                _products.value = current
                Result.success(updatedLocally)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun quickUpdatePrice(productId: String, newPrice: Double, newOriginalPrice: Double?): Result<Product> = withContext(Dispatchers.IO) {
        val target = _products.value.find { it.id == productId }
            ?: return@withContext Result.failure(Exception("Product not found: $productId"))
        val payload = ApiProductPayload(
            name = target.name,
            sku = target.sku.ifBlank { null },
            slug = target.slug.ifBlank { null },
            category = target.category.displayName,
            productType = target.productType,
            description = target.fullDescription,
            shortDescription = target.shortDescription,
            detailedDescription = target.fullDescription,
            price = newPrice,
            originalPrice = newOriginalPrice,
            compareAtPrice = newOriginalPrice,
            currency = target.currency,
            discountPercent = if (newOriginalPrice != null && newOriginalPrice > newPrice) {
                (((newOriginalPrice - newPrice) / newOriginalPrice) * 100).toInt()
            } else 0,
            image = target.imageUrl,
            tags = listOf("Verified", target.category.displayName),
            digital = target.instantDelivery,
            stock = 50,
            status = if (target.inStock) "active" else "out_of_stock",
            active = target.active
        )
        updateProductContentAndPrice(productId, payload)
    }

    suspend fun deleteProduct(productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val token = _adminToken.value
        addLog("Deleting product $productId from ${_serverUrl.value}...")
        try {
            val authHeader = if (!token.isNullOrBlank()) "Bearer $token" else ""
            val response = PlaybeatApiClient.getService().deleteProduct(authHeader, productId)
            if (response.success) {
                val current = _products.value.toMutableList()
                current.removeAll { it.id == productId || it.sku == productId || it.slug == productId }
                _products.value = current
                addLog("Product $productId deleted successfully from MongoDB")
                Result.success(Unit)
            } else {
                val err = response.error ?: response.message ?: "Failed to delete product"
                addLog("Delete notice: $err (removed locally)")
                val current = _products.value.toMutableList()
                current.removeAll { it.id == productId || it.sku == productId || it.slug == productId }
                _products.value = current
                Result.success(Unit)
            }
        } catch (e: Exception) {
            val err = e.message ?: "Network error deleting product"
            addLog("Delete notice: $err (removed locally)")
            val current = _products.value.toMutableList()
            current.removeAll { it.id == productId || it.sku == productId || it.slug == productId }
            _products.value = current
            Result.success(Unit)
        }
    }

    fun setServerBaseUrl(url: String) {
        PlaybeatApiClient.setBaseUrl(url)
        _serverUrl.value = PlaybeatApiClient.getBaseUrl()
        addLog("Server endpoint changed to: ${_serverUrl.value}")
        repositoryScope.launch {
            refreshLiveProducts()
        }
    }

    // Auth
    fun login(email: String, name: String = "Alex Vance") {
        _userProfile.value = _userProfile.value.copy(
            email = email,
            name = name,
            isAuthenticated = true
        )
    }

    fun logout() {
        _userProfile.value = _userProfile.value.copy(
            isAuthenticated = false
        )
    }
}
