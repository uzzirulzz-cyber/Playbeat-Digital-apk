package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SyncState
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.network.ApiAdminUser
import com.example.network.ApiProductPayload
import com.example.network.PlaybeatApiClient
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyBlack
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PlaybeatBlue
import com.example.ui.theme.PlaybeatOrange
import com.example.ui.theme.SlateBorderSubtle
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun AdminConsoleScreen(
    products: List<Product>,
    syncState: SyncState,
    adminToken: String?,
    adminUser: ApiAdminUser?,
    serverUrl: String,
    activityLogs: List<String>,
    onBackClick: () -> Unit,
    onRefreshCatalog: () -> Unit,
    onAdminLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onSetAdminToken: (String) -> Unit,
    onAdminLogout: () -> Unit,
    onPostProduct: (ApiProductPayload, (Boolean, String?) -> Unit) -> Unit,
    onUpdateProduct: (String, ApiProductPayload, (Boolean, String?) -> Unit) -> Unit,
    onQuickUpdatePrice: (String, Double, Double?, (Boolean, String?) -> Unit) -> Unit,
    onDeleteProduct: (String, (Boolean, String?) -> Unit) -> Unit,
    onUpdateServerUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog states
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var deletingProduct by remember { mutableStateOf<Product?>(null) }
    var showServerUrlDialog by remember { mutableStateOf(false) }

    val tabTitles = listOf("Products & Prices", "Post New Product", "Activity Log")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBlack)
            .testTag("admin_console_screen")
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Store",
                        tint = TextPrimary
                    )
                }

                Column {
                    Text(
                        text = "PlayBeat Admin Console",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "playbeat.digital/admin • MongoDB",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PlaybeatBlue,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Sync Button
            IconButton(
                onClick = onRefreshCatalog,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NavySurface)
                    .border(1.dp, SlateBorderSubtle, CircleShape)
                    .testTag("admin_sync_button")
            ) {
                if (syncState is SyncState.Syncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = PlaybeatBlue,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sync",
                        tint = PlaybeatBlue
                    )
                }
            }
        }

        // SERVER & ADMIN STATUS STRIP
        ServerStatusCard(
            syncState = syncState,
            serverUrl = serverUrl,
            adminUser = adminUser,
            adminToken = adminToken,
            onOpenAdminWeb = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PlaybeatApiClient.ADMIN_PORTAL_URL))
                context.startActivity(intent)
            },
            onChangeEndpointClick = { showServerUrlDialog = true }
        )

        // TABS
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = NavyBlack,
            contentColor = PlaybeatBlue,
            edgePadding = 16.dp,
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) PlaybeatBlue else TextSecondary,
                                fontSize = 13.sp
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TAB CONTENT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> ProductsAndPricesTab(
                    products = products,
                    onEditClick = { editingProduct = it },
                    onDeleteClick = { deletingProduct = it },
                    onQuickPriceUpdate = { product, newPrice, newOrigPrice ->
                        onQuickUpdatePrice(product.id, newPrice, newOrigPrice) { success, msg ->
                            Toast.makeText(context, msg ?: if (success) "Price updated" else "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                1 -> PostProductTab(
                    onPostProduct = { payload ->
                        onPostProduct(payload) { success, msg ->
                            Toast.makeText(context, msg ?: if (success) "Product posted!" else "Failed", Toast.LENGTH_LONG).show()
                            if (success) {
                                selectedTab = 0
                            }
                        }
                    }
                )
                2 -> ActivityLogTab(
                    logs = activityLogs,
                    serverUrl = serverUrl,
                    adminToken = adminToken,
                    onAdminLogin = onAdminLogin,
                    onSetAdminToken = onSetAdminToken,
                    onAdminLogout = onAdminLogout
                )
            }
        }
    }

    // Edit Product Dialog
    editingProduct?.let { product ->
        EditProductDialog(
            product = product,
            onDismiss = { editingProduct = null },
            onSave = { updatedPayload ->
                onUpdateProduct(product.id, updatedPayload) { success, msg ->
                    Toast.makeText(context, msg ?: if (success) "Updated!" else "Failed", Toast.LENGTH_SHORT).show()
                    if (success) {
                        editingProduct = null
                    }
                }
            }
        )
    }

    // Delete Product Dialog
    deletingProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { deletingProduct = null },
            containerColor = NavySurface,
            title = {
                Text("Delete Product?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to remove '${product.name}' from the MongoDB catalog? This will delete it on playbeat.digital.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toDel = deletingProduct ?: return@Button
                        onDeleteProduct(toDel.id) { success, msg ->
                            Toast.makeText(context, msg ?: "Deleted", Toast.LENGTH_SHORT).show()
                            deletingProduct = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete from Server", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deletingProduct = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Change Server URL Dialog
    if (showServerUrlDialog) {
        var tempUrl by remember { mutableStateOf(serverUrl) }
        AlertDialog(
            onDismissRequest = { showServerUrlDialog = false },
            containerColor = NavySurface,
            title = {
                Text("Backend Server Endpoint", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Set the API server address (default is playbeat.digital).",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PlaybeatBlue,
                            unfocusedBorderColor = SlateBorderSubtle
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { tempUrl = "https://playbeat.digital/" },
                            colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant)
                        ) {
                            Text("Reset Default", fontSize = 11.sp, color = PlaybeatBlue)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateServerUrl(tempUrl)
                        showServerUrlDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue)
                ) {
                    Text("Save & Sync", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showServerUrlDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ServerStatusCard(
    syncState: SyncState,
    serverUrl: String,
    adminUser: ApiAdminUser?,
    adminToken: String?,
    onOpenAdminWeb: () -> Unit,
    onChangeEndpointClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connection indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dotColor = when (syncState) {
                        is SyncState.Connected -> SuccessGreen
                        is SyncState.Syncing -> PlaybeatBlue
                        else -> PlaybeatOrange
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Text(
                        text = when (syncState) {
                            is SyncState.Connected -> "LIVE CONNECTED (${syncState.productCount} items)"
                            is SyncState.Syncing -> "SYNCING WITH MONGODB..."
                            is SyncState.Error -> "STANDBY (Using Cached Items)"
                            else -> "READY"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = dotColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        )
                    )
                }

                // Auth status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (adminToken != null) PlaybeatBlue.copy(alpha = 0.2f) else NavySurfaceVariant)
                        .border(1.dp, if (adminToken != null) PlaybeatBlue else SlateBorderSubtle, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (adminToken != null) "ADMIN AUTH: ACTIVE" else "SESSION: READ-ONLY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (adminToken != null) PlaybeatBlue else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Server URL & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Target Endpoint:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                    )
                    Text(
                        text = serverUrl,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onChangeEndpointClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Config",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Button(
                        onClick = onOpenAdminWeb,
                        colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Web",
                                tint = PlaybeatBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Open Admin Web",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PlaybeatBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductsAndPricesTab(
    products: List<Product>,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit,
    onQuickPriceUpdate: (Product, Double, Double?) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(products, query) {
        if (query.isBlank()) products
        else products.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.category.displayName.contains(query, ignoreCase = true) ||
            it.sku.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search catalog to edit prices...", color = TextSecondary, fontSize = 12.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = PlaybeatBlue, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("admin_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PlaybeatBlue,
                unfocusedBorderColor = SlateBorderSubtle,
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filtered.size} items in live catalog",
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp)
            )
            Text(
                text = "Tap 'Edit' to update price on server",
                style = MaterialTheme.typography.labelSmall.copy(color = PlaybeatOrange, fontSize = 11.sp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("admin_products_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            items(filtered, key = { it.id }) { product ->
                AdminProductCard(
                    product = product,
                    onEdit = { onEditClick(product) },
                    onDelete = { onDeleteClick(product) },
                    onQuickPriceBump = { delta ->
                        val newP = (product.basePrice + delta).coerceAtLeast(1.0)
                        val origP = product.originalPrice?.let { (it + delta).coerceAtLeast(newP) }
                        onQuickPriceUpdate(product, newP, origP)
                    }
                )
            }
        }
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuickPriceBump: (Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_product_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavySurfaceVariant)
                ) {
                    if (product.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = when (product.category) {
                                ProductCategory.PROJECTORS -> "📽️"
                                ProductCategory.GAMING -> "🎮"
                                ProductCategory.SOFTWARE -> "⚡"
                                ProductCategory.GIFT_CARDS -> "💳"
                                ProductCategory.STREAMING -> "🎬"
                                else -> "📦"
                            },
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 20.sp
                        )
                    }
                }

                // Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        maxLines = 1
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.category.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PlaybeatBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (product.sku.isNotBlank()) {
                            Text(
                                text = "• ${product.sku}",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                            )
                        }
                    }
                }

                // Price display
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (product.currency == "PKR") {
                            "PKR ${String.format(Locale.US, "%,.0f", product.basePrice)}"
                        } else {
                            "$${String.format(Locale.US, "%.2f", product.basePrice)}"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    if (product.originalPrice != null && product.originalPrice > product.basePrice) {
                        Text(
                            text = if (product.currency == "PKR") {
                                "PKR ${String.format(Locale.US, "%,.0f", product.originalPrice)}"
                            } else {
                                "$${String.format(Locale.US, "%.2f", product.originalPrice)}"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick + / - step
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quick:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NavySurfaceVariant)
                            .clickable { onQuickPriceBump(-100.0) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("-100", color = TextSecondary, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NavySurfaceVariant)
                            .clickable { onQuickPriceBump(100.0) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("+100", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                // Edit and Delete Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(12.dp), tint = Color.White)
                            Text("Edit Price", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (ApiProductPayload) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var priceText by remember { mutableStateOf(product.basePrice.toInt().toString()) }
    var origPriceText by remember { mutableStateOf(product.originalPrice?.toInt()?.toString() ?: "") }
    var description by remember { mutableStateOf(product.shortDescription) }
    var inStock by remember { mutableStateOf(product.inStock) }
    var instantKey by remember { mutableStateOf(product.instantDelivery) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = {
            Text("Edit Product & Price", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Title", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PlaybeatBlue,
                        unfocusedBorderColor = SlateBorderSubtle
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (${product.currency})", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PlaybeatBlue,
                            unfocusedBorderColor = SlateBorderSubtle
                        )
                    )

                    OutlinedTextField(
                        value = origPriceText,
                        onValueChange = { origPriceText = it },
                        label = { Text("Original / Strike", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PlaybeatBlue,
                            unfocusedBorderColor = SlateBorderSubtle
                        )
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Short Description", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PlaybeatBlue,
                        unfocusedBorderColor = SlateBorderSubtle
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("In Stock Status", color = TextSecondary, fontSize = 12.sp)
                    Switch(
                        checked = inStock,
                        onCheckedChange = { inStock = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PlaybeatBlue)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Instant Key Delivery", color = TextSecondary, fontSize = 12.sp)
                    Switch(
                        checked = instantKey,
                        onCheckedChange = { instantKey = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PlaybeatOrange)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedPrice = priceText.toDoubleOrNull() ?: product.basePrice
                    val parsedOrig = origPriceText.toDoubleOrNull()
                    val payload = ApiProductPayload(
                        name = name.ifBlank { product.name },
                        sku = product.sku.ifBlank { null },
                        slug = product.slug.ifBlank { null },
                        category = product.category.displayName,
                        productType = product.productType,
                        description = description,
                        shortDescription = description,
                        detailedDescription = product.fullDescription,
                        price = parsedPrice,
                        originalPrice = parsedOrig,
                        compareAtPrice = parsedOrig,
                        currency = product.currency,
                        discountPercent = if (parsedOrig != null && parsedOrig > parsedPrice) {
                            (((parsedOrig - parsedPrice) / parsedOrig) * 100).toInt()
                        } else 0,
                        image = product.imageUrl,
                        tags = listOf("Verified", product.category.displayName),
                        digital = instantKey,
                        stock = if (inStock) 50 else 0,
                        status = if (inStock) "active" else "out_of_stock",
                        active = inStock
                    )
                    onSave(payload)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue)
            ) {
                Text("Save to playbeat.digital", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun PostProductTab(
    onPostProduct: (ApiProductPayload) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("Software") }
    var priceText by remember { mutableStateOf("1999") }
    var origPriceText by remember { mutableStateOf("2499") }
    var shortDesc by remember { mutableStateOf("") }
    var detailedDesc by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("/assets/images/products/software.webp") }
    var isDigital by remember { mutableStateOf(true) }
    var stockText by remember { mutableStateOf("100") }

    val categories = listOf("Software", "Subscriptions", "Streaming", "Smart Projectors", "Gaming", "Gift Cards")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_post_product_form"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Publish to PlayBeat MongoDB Catalog",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Pushes directly to POST /api/admin/products on playbeat.digital",
                style = MaterialTheme.typography.labelSmall.copy(color = PlaybeatBlue, fontSize = 11.sp)
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name *") },
                placeholder = { Text("e.g. Proton VPN Plus 1 Year") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PlaybeatBlue,
                    unfocusedBorderColor = SlateBorderSubtle
                )
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Category:", color = TextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (categoryName == cat) PlaybeatBlue else NavySurfaceVariant)
                                .clickable { categoryName = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                color = if (categoryName == cat) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (categoryName == cat) PlaybeatBlue else NavySurfaceVariant)
                                .clickable { categoryName = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                color = if (categoryName == cat) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (PKR) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PlaybeatBlue,
                        unfocusedBorderColor = SlateBorderSubtle
                    )
                )

                OutlinedTextField(
                    value = origPriceText,
                    onValueChange = { origPriceText = it },
                    label = { Text("Compare Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PlaybeatBlue,
                        unfocusedBorderColor = SlateBorderSubtle
                    )
                )
            }
        }

        item {
            OutlinedTextField(
                value = shortDesc,
                onValueChange = { shortDesc = it },
                label = { Text("Short Description") },
                placeholder = { Text("Instant 60s activation key with 1 year warranty") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PlaybeatBlue,
                    unfocusedBorderColor = SlateBorderSubtle
                )
            )
        }

        item {
            OutlinedTextField(
                value = detailedDesc,
                onValueChange = { detailedDesc = it },
                label = { Text("Detailed Specifications / Guide") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PlaybeatBlue,
                    unfocusedBorderColor = SlateBorderSubtle
                )
            )
        }

        item {
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Product Image / Path") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PlaybeatBlue,
                    unfocusedBorderColor = SlateBorderSubtle
                )
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Digital Auto-Fulfillment", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Issues automated digital keys immediately on purchase", color = TextMuted, fontSize = 11.sp)
                }
                Switch(
                    checked = isDigital,
                    onCheckedChange = { isDigital = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = PlaybeatOrange)
                )
            }
        }

        item {
            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull() ?: 1000.0
                    val op = origPriceText.toDoubleOrNull()
                    val payload = ApiProductPayload(
                        name = name.ifBlank { "New Digital Service" },
                        category = categoryName,
                        productType = if (isDigital) "digital" else "physical",
                        description = detailedDesc.ifBlank { shortDesc.ifBlank { "PlayBeat certified product." } },
                        shortDescription = shortDesc.ifBlank { "Instant automated digital key" },
                        detailedDescription = detailedDesc.ifBlank { shortDesc },
                        price = p,
                        originalPrice = op,
                        compareAtPrice = op,
                        currency = "PKR",
                        discountPercent = if (op != null && op > p) (((op - p) / op) * 100).toInt() else 0,
                        image = imageUrl.ifBlank { "/assets/images/products/software.webp" },
                        tags = listOf("Verified", categoryName, if (isDigital) "Digital" else "Hardware"),
                        digital = isDigital,
                        stock = stockText.toIntOrNull() ?: 50,
                        status = "active",
                        active = true,
                        deliveryType = if (isDigital) "Instant Auto-Email" else "Courier Shipping (1-3 Days)",
                        deliveryInfo = if (isDigital) "Instant 15-Second Key Delivery" else "Courier shipping tracking included"
                    )
                    onPostProduct(payload)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_submit_product_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    Text("Publish to playbeat.digital", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ActivityLogTab(
    logs: List<String>,
    serverUrl: String,
    adminToken: String?,
    onAdminLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onSetAdminToken: (String) -> Unit,
    onAdminLogout: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("admin@playbeat.digital") }
    var password by remember { mutableStateOf("") }
    var manualToken by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_activity_log_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Auth Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "PlayBeat Admin Credentials",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Admin routes (/api/admin/*) on playbeat.digital require administrative authentication from the izoko backend.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )

                    if (adminToken != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.15f))
                                .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Text("Session Active (Bearer Token Set)", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onAdminLogout,
                                colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Sign Out", color = ErrorRed, fontSize = 11.sp)
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Admin Email", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PlaybeatBlue,
                                unfocusedBorderColor = SlateBorderSubtle
                            )
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Admin Password", fontSize = 11.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PlaybeatBlue,
                                unfocusedBorderColor = SlateBorderSubtle
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    isSubmitting = true
                                    onAdminLogin(email, password) { success, err ->
                                        isSubmitting = false
                                        if (success) {
                                            Toast.makeText(context, "Logged in as Admin!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, err ?: "Auth failed. You can also paste token below.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue),
                                enabled = !isSubmitting && email.isNotBlank() && password.isNotBlank()
                            ) {
                                Text(if (isSubmitting) "Verifying..." else "Login as Admin", color = Color.White)
                            }
                        }

                        // Direct Token option
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Or enter Bearer Token from playbeat.digital/admin:", color = TextMuted, fontSize = 10.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = manualToken,
                                    onValueChange = { manualToken = it },
                                    placeholder = { Text("Paste JWT Token", color = TextSecondary, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PlaybeatBlue,
                                        unfocusedBorderColor = SlateBorderSubtle
                                    ),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        if (manualToken.isNotBlank()) {
                                            onSetAdminToken(manualToken)
                                            Toast.makeText(context, "Token saved!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant)
                                ) {
                                    Text("Apply", color = PlaybeatBlue)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Log Stream
        item {
            Text(
                text = "Live Sync & Server Response Logs",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        items(logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = log,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (log.contains("fail", ignoreCase = true) || log.contains("error", ignoreCase = true)) {
                            ErrorRed
                        } else if (log.contains("success", ignoreCase = true) || log.contains("synced", ignoreCase = true)) {
                            SuccessGreen
                        } else {
                            TextSecondary
                        },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}
