package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.ui.components.CategoryPillRow
import com.example.ui.components.HeroBanner
import com.example.ui.components.ProductCard
import com.example.ui.theme.NavyBlack
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PlaybeatBlue
import com.example.ui.theme.PlaybeatBlueLight
import com.example.ui.theme.PlaybeatOrange
import com.example.ui.theme.SlateBorderSubtle
import com.example.ui.theme.SlateButtonBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary


@Composable
fun HomeScreen(
    products: List<Product>,
    wishlist: Set<String>,
    selectedCategory: ProductCategory,
    onCategorySelect: (ProductCategory) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onWishlistToggle: (String) -> Unit,
    onSearchClick: () -> Unit,
    onExploreDealsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trending = products.filter { it.isTrending }
    val bestSellers = products.filter { it.isBestSeller }
    val specialOffers = products.filter { it.isSpecialOffer }
    val newArrivals = products.filter { it.isNewArrival }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBlack),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Quick Search Bar Teaser
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateButtonBg)
                    .border(1.dp, SlateBorderSubtle, RoundedCornerShape(16.dp))
                    .clickable { onSearchClick() }
                    .padding(horizontal = 16.dp, vertical = 13.dp)
                    .testTag("home_search_teaser")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Search digital products...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // Hero Promotional Banner
        item {
            HeroBanner(
                onExploreClick = onExploreDealsClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Featured Categories Header & Pills
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = "Categories",
                subtitle = "Browse digital catalog",
                icon = Icons.Default.Bolt,
                onSeeAllClick = onSearchClick
            )
            Spacer(modifier = Modifier.height(6.dp))
            CategoryPillRow(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelect
            )
        }

        // Trending Products Section
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Trending Now",
                subtitle = "Highest demand keys today",
                icon = Icons.Default.TrendingUp,
                onSeeAllClick = onSearchClick
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(trending) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlist.contains(product.id),
                        onProductClick = { onProductClick(product) },
                        onAddToCartClick = { onAddToCart(product) },
                        onWishlistToggle = { onWishlistToggle(product.id) },
                        modifier = Modifier.width(230.dp)
                    )
                }
            }
        }

        // Special Offers Section
        item {
            Spacer(modifier = Modifier.height(22.dp))
            SectionHeader(
                title = "Special Offers",
                subtitle = "Up to 70% off retail",
                icon = Icons.Default.LocalOffer,
                onSeeAllClick = onSearchClick
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(specialOffers) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlist.contains(product.id),
                        onProductClick = { onProductClick(product) },
                        onAddToCartClick = { onAddToCart(product) },
                        onWishlistToggle = { onWishlistToggle(product.id) },
                        modifier = Modifier.width(230.dp)
                    )
                }
            }
        }

        // Best Sellers Section
        item {
            Spacer(modifier = Modifier.height(22.dp))
            SectionHeader(
                title = "Best Sellers",
                subtitle = "Top verified customer picks",
                icon = Icons.Default.Bolt,
                onSeeAllClick = onSearchClick
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(bestSellers) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlist.contains(product.id),
                        onProductClick = { onProductClick(product) },
                        onAddToCartClick = { onAddToCart(product) },
                        onWishlistToggle = { onWishlistToggle(product.id) },
                        modifier = Modifier.width(230.dp)
                    )
                }
            }
        }

        // New Arrivals Section
        item {
            Spacer(modifier = Modifier.height(22.dp))
            SectionHeader(
                title = "New Arrivals",
                subtitle = "Freshly added licenses & passes",
                icon = Icons.Default.TrendingUp,
                onSeeAllClick = onSearchClick
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(newArrivals) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlist.contains(product.id),
                        onProductClick = { onProductClick(product) },
                        onAddToCartClick = { onAddToCart(product) },
                        onWishlistToggle = { onWishlistToggle(product.id) },
                        modifier = Modifier.width(230.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Text(
            text = "View All",
            style = MaterialTheme.typography.labelMedium.copy(
                color = PlaybeatBlueLight,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .clickable { onSeeAllClick() }
                .padding(vertical = 4.dp)
        )
    }
}
