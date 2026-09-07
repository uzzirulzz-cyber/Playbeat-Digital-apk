package com.example

import com.example.data.PlaybeatRepository
import com.example.model.PaymentMethod
import com.example.model.ProductCategory
import com.example.model.ProductPlan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PlaybeatRepositoryTest {

    private lateinit var repository: PlaybeatRepository

    @Before
    fun setUp() {
        repository = PlaybeatRepository()
    }

    @Test
    fun testInitialProductsLoaded() {
        val products = repository.products.value
        assertTrue("Products list should not be empty", products.isNotEmpty())
        assertTrue("Should contain Steam Keys", products.any { it.category == ProductCategory.GAMING })
        assertTrue("Should contain Software licenses", products.any { it.category == ProductCategory.SOFTWARE })
    }

    @Test
    fun testAddToCartAndQuantity() {
        val product = repository.products.value.first()
        val plan = product.plans.first()

        repository.addToCart(product, plan, 2)
        assertEquals(1, repository.cart.value.size)
        assertEquals(2, repository.cart.value.first().quantity)

        // Increment quantity
        val item = repository.cart.value.first()
        repository.updateCartQuantity(item, 3)
        assertEquals(3, repository.cart.value.first().quantity)

        // Remove item
        repository.removeFromCart(repository.cart.value.first())
        assertTrue(repository.cart.value.isEmpty())
    }

    @Test
    fun testCoupons() {
        assertTrue(repository.applyCoupon("PLAYBEAT10"))
        assertEquals("PLAYBEAT10", repository.appliedCoupon.value)

        assertTrue(repository.applyCoupon("WELCOME20"))
        assertEquals("WELCOME20", repository.appliedCoupon.value)

        assertFalse(repository.applyCoupon("INVALID_CODE"))

        repository.removeCoupon()
        assertEquals(null, repository.appliedCoupon.value)
    }

    @Test
    fun testWishlistToggle() {
        val testProductId = "game_2" // Not in initial setOf("soft_1", "game_1")
        assertFalse(repository.wishlist.value.contains(testProductId))

        repository.toggleWishlist(testProductId)
        assertTrue(repository.wishlist.value.contains(testProductId))

        repository.toggleWishlist(testProductId)
        assertFalse(repository.wishlist.value.contains(testProductId))
    }

    @Test
    fun testPlaceOrderGeneratesKeys() {
        val product = repository.products.value.first()
        val plan = product.plans.first()
        repository.addToCart(product, plan, 1)

        val initialOrdersCount = repository.orders.value.size

        val newOrder = repository.placeOrder(
            paymentMethod = PaymentMethod.CREDIT_CARD,
            customerEmail = "test@playbeat.digital",
            subtotal = plan.price,
            discount = 0.0,
            total = plan.price
        )

        assertNotNull(newOrder)
        assertEquals(initialOrdersCount + 1, repository.orders.value.size)
        assertTrue(repository.cart.value.isEmpty()) // Cart cleared
        val generatedKey = newOrder.items.first().digitalLicense.licenseKey
        assertTrue("License key should not be blank", generatedKey.isNotBlank())
        assertTrue("License key should have formatted parts", generatedKey.contains("-"))
    }
}
