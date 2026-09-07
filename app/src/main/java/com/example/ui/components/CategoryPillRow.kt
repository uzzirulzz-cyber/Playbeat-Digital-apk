package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
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
import com.example.model.ProductCategory
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.PlaybeatBlue
import com.example.ui.theme.PlaybeatSilver
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CategoryPillRow(
    selectedCategory: ProductCategory,
    onCategorySelected: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = ProductCategory.values()

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { cat ->
            val isSelected = cat == selectedCategory
            val icon = getCategoryIcon(cat)

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onCategorySelected(cat) }
                    .testTag("cat_pill_${cat.name.lowercase()}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) PlaybeatBlue.copy(alpha = 0.16f) else NavySurface
                        )
                        .border(
                            1.dp,
                            if (isSelected) PlaybeatBlue.copy(alpha = 0.6f) else NavyCardBorder,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = cat.displayName,
                        tint = if (isSelected) PlaybeatBlue else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = cat.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSelected) PlaybeatBlue else PlaybeatSilver,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

private fun getCategoryIcon(category: ProductCategory): ImageVector {
    return when (category) {
        ProductCategory.ALL -> Icons.Default.Apps
        ProductCategory.GAMING -> Icons.Default.SportsEsports
        ProductCategory.SOFTWARE -> Icons.Default.Terminal
        ProductCategory.GIFT_CARDS -> Icons.Default.CardGiftcard
        ProductCategory.STREAMING -> Icons.Default.LiveTv
        ProductCategory.SOCIAL_MEDIA -> Icons.Default.Share
        ProductCategory.WEB_HOSTING -> Icons.Default.Dns
        ProductCategory.DIGITAL_MARKETING -> Icons.Default.Campaign
        ProductCategory.WEB3 -> Icons.Default.CurrencyBitcoin
        ProductCategory.PROJECTORS -> Icons.Default.LiveTv
        ProductCategory.SUBSCRIPTIONS -> Icons.Default.MiscellaneousServices
        ProductCategory.SERVICES -> Icons.Default.MiscellaneousServices
        else -> Icons.Default.Apps
    }
}
