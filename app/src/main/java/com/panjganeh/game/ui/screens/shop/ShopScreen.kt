package com.panjganeh.game.ui.screens.shop

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.billing.BazaarBillingManager
import com.panjganeh.game.billing.BazaarConfig
import com.panjganeh.game.billing.PurchaseResult
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.EmeraldTertiary
import com.panjganeh.game.ui.theme.GoldDark
import com.panjganeh.game.ui.theme.GoldLight
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.SkySecondary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.VipGold

@Composable
fun ShopScreen(
    userRepository: UserRepository,
    billingManager: BazaarBillingManager,
    tapsellManager: TapsellManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(Unit) {
        billingManager.purchaseEvents.collect { event ->
            when (event) {
                is PurchaseResult.Success -> Toast.makeText(context, event.messageFa, Toast.LENGTH_LONG).show()
                is PurchaseResult.Error -> Toast.makeText(context, event.messageFa, Toast.LENGTH_LONG).show()
                PurchaseResult.Cancelled -> Toast.makeText(context, "تراکنش لغو شد", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = ArenaBackground,
        topBar = {
            ArenaTopBar(
                user = user,
                vip = vip,
                title = "فروشگاه آرنا",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // بخش دریافت سکه رایگان با ویدیوی تپسل
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rewarded_video_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldTertiary.copy(alpha = 0.15f)),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(EmeraldTertiary))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldTertiary.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.OndemandVideo, contentDescription = null, tint = EmeraldTertiary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "سکه طلا رایگان!", color = EmeraldTertiary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "تماشای ویدیوی تبلیغاتی تپسل (+۱۵۰ سکه)", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (activity != null) {
                                    tapsellManager.showRewardedVideo(activity) { rewardAmount ->
                                        Toast.makeText(context, "+$rewardAmount سکه طلا دریافت کردید!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldTertiary, contentColor = Color.Black),
                            modifier = Modifier.testTag("watch_ad_button")
                        ) {
                            Text(text = "تماشا", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "محصولات پرداخت درون‌برنامه‌ای کافه‌بازار",
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // لیست محصولات بازار
            items(BazaarConfig.ALL_PRODUCTS, key = { it.sku }) { product ->
                ProductCard(
                    product = product,
                    onBuyClick = {
                        if (activity != null) {
                            billingManager.purchaseProduct(activity, product.sku)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: BazaarConfig.ProductInfo,
    onBuyClick: () -> Unit
) {
    val isVipProduct = product.iconType == "VIP"
    val iconVector = when (product.iconType) {
        "VIP" -> Icons.Default.WorkspacePremium
        "COIN" -> Icons.Default.MonetizationOn
        else -> Icons.Default.ConfirmationNumber
    }
    val iconColor = when (product.iconType) {
        "VIP" -> VipGold
        "COIN" -> GoldPrimary
        else -> SkySecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.sku}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isVipProduct) Brush.horizontalGradient(listOf(VipGold, GoldDark)) else Brush.horizontalGradient(listOf(ArenaSurfaceBorder, ArenaSurfaceBorder))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f))
                        .border(1.5.dp, iconColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = iconVector, contentDescription = null, tint = iconColor, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = product.titleFa, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (product.badgeFa != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldPrimary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = product.badgeFa, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(text = product.descriptionFa, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onBuyClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVipProduct) VipGold else GoldPrimary,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("buy_btn_${product.sku}")
            ) {
                Text(text = product.priceFormatted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
