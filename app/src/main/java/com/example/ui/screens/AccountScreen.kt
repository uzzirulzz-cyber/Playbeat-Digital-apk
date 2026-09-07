package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NotificationPreferences
import com.example.model.UserProfile
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyBlack
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PlaybeatBlue
import com.example.ui.theme.PlaybeatOrange
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AccountScreen(
    userProfile: UserProfile,
    orderCount: Int,
    wishlistCount: Int,
    notifications: NotificationPreferences,
    onNotificationUpdate: (NotificationPreferences) -> Unit,
    onViewOrdersClick: () -> Unit,
    onViewWishlistClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    onLogoutClick: () -> Unit,
    onOpenAdminConsole: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showNotifDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyCardBorder),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PlaybeatOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userProfile.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SuccessGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Verified VIP",
                                        color = SuccessGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = userProfile.email,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = NavyCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AccountStatItem(
                            label = "Orders",
                            value = "$orderCount",
                            onClick = onViewOrdersClick
                        )
                        AccountStatItem(
                            label = "Wishlist",
                            value = "$wishlistCount",
                            onClick = onViewWishlistClick
                        )
                        AccountStatItem(
                            label = "Store Credit",
                            value = "$${String.format("%.2f", userProfile.walletBalance)}",
                            onClick = { Toast.makeText(context, "Playbeat Wallet Active", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
            }
        }

        // Section: Store Services & Licenses
        item {
            Text(
                text = "Digital Services & Preferences",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyCardBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    AccountActionRow(
                        title = "Order History & Digital Keys",
                        subtitle = "Access all purchased software keys & passes",
                        icon = Icons.Default.ReceiptLong,
                        iconTint = PlaybeatBlue,
                        onClick = onViewOrdersClick
                    )
                    Divider(color = NavyCardBorder, thickness = 0.8.dp)

                    AccountActionRow(
                        title = "Saved Wishlist",
                        subtitle = "$wishlistCount items saved for later",
                        icon = Icons.Default.Favorite,
                        iconTint = ErrorRed,
                        onClick = onViewWishlistClick
                    )
                    Divider(color = NavyCardBorder, thickness = 0.8.dp)

                    AccountActionRow(
                        title = "Push Notifications & Alerts",
                        subtitle = "Key delivery, payment status & exclusive deals",
                        icon = Icons.Default.Notifications,
                        iconTint = PlaybeatOrange,
                        onClick = { showNotifDialog = true }
                    )
                    Divider(color = NavyCardBorder, thickness = 0.8.dp)

                    AccountActionRow(
                        title = "24/7 Dedicated Support",
                        subtitle = "WhatsApp & Email instant response",
                        icon = Icons.Default.HelpCenter,
                        iconTint = SuccessGreen,
                        onClick = { showSupportDialog = true }
                    )
                }
            }
        }

        // Section: PlayBeat Cloud & Admin
        if (onOpenAdminConsole != null) {
            item {
                Text(
                    text = "Cloud Management",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, PlaybeatBlue.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable { onOpenAdminConsole() }
                ) {
                    AccountActionRow(
                        title = "PlayBeat Admin Console",
                        subtitle = "Connected to playbeat.digital • Manage products & prices",
                        icon = Icons.Default.CloudSync,
                        iconTint = PlaybeatBlue,
                        onClick = onOpenAdminConsole
                    )
                }
            }
        }

        // Section: Legal & Compliance
        item {
            Text(
                text = "Security & Policies",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyCardBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    AccountActionRow(
                        title = "Privacy Policy",
                        subtitle = "Data handling & zero-credential storage policy",
                        icon = Icons.Default.Policy,
                        iconTint = TextSecondary,
                        onClick = { showPrivacyDialog = true }
                    )
                    Divider(color = NavyCardBorder, thickness = 0.8.dp)

                    AccountActionRow(
                        title = "Terms of Service",
                        subtitle = "Warranty, returns & license activation terms",
                        icon = Icons.Default.Shield,
                        iconTint = TextSecondary,
                        onClick = { showTermsDialog = true }
                    )
                    Divider(color = NavyCardBorder, thickness = 0.8.dp)

                    AccountActionRow(
                        title = "Sign Out",
                        subtitle = "Disconnect current account session",
                        icon = Icons.Default.Logout,
                        iconTint = ErrorRed,
                        onClick = {
                            onLogoutClick()
                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Push Notifications Dialog
    if (showNotifDialog) {
        AlertDialog(
            onDismissRequest = { showNotifDialog = false },
            containerColor = NavySurface,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = PlaybeatOrange)
                    Text("Push Notification Preferences", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    NotificationToggleRow(
                        title = "Digital Key Delivery Alerts",
                        subtitle = "Instant push as soon as keys are ready",
                        checked = notifications.digitalDelivery,
                        onCheckedChange = {
                            onNotificationUpdate(notifications.copy(digitalDelivery = it))
                        }
                    )
                    NotificationToggleRow(
                        title = "Order & Payment Status",
                        subtitle = "Updates on order confirmations",
                        checked = notifications.orderUpdates,
                        onCheckedChange = {
                            onNotificationUpdate(notifications.copy(orderUpdates = it))
                        }
                    )
                    NotificationToggleRow(
                        title = "Special Offers & Flash Sales",
                        subtitle = "Discounts on trending licenses",
                        checked = notifications.promoDiscounts,
                        onCheckedChange = {
                            onNotificationUpdate(notifications.copy(promoDiscounts = it))
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotifDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue)
                ) {
                    Text("Save Preferences", color = NavyBlack, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            containerColor = NavySurface,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.HelpCenter, contentDescription = null, tint = SuccessGreen)
                    Text("Playbeat 24/7 Support", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Need assistance with a license key, redemption, or custom bulk orders? Our team is available 24/7.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Text("WhatsApp Hotline: +92 300 1234567", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = PlaybeatBlue, modifier = Modifier.size(18.dp))
                                Text("Email: support@playbeat.digital", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Opening Playbeat Digital WhatsApp...", Toast.LENGTH_SHORT).show()
                        showSupportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Chat on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSupportDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = NavySurface,
            title = { Text("Privacy Policy", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Playbeat Digital operates under strict privacy safeguards. We never store credit/debit card credentials. All transactions use 256-bit SSL encryption. Digital keys are dispatched via automated backend systems with full end-to-end security.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue)) {
                    Text("Close", color = NavyBlack)
                }
            }
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            containerColor = NavySurface,
            title = { Text("Terms & Conditions", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "All digital products sold on Playbeat Digital are 100% verified distributor keys. In the rare event of an activation issue, our 24/7 support team provides immediate key replacement within the guaranteed warranty duration.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PlaybeatBlue)) {
                    Text("I Agree", color = NavyBlack)
                }
            }
        )
    }
}

@Composable
fun AccountStatItem(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = PlaybeatBlue
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun AccountActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NavySurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PlaybeatOrange,
                checkedTrackColor = PlaybeatOrange.copy(alpha = 0.3f),
                uncheckedTrackColor = NavySurfaceVariant
            )
        )
    }
}
