package com.panjganeh.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panjganeh.game.data.local.entity.UserProfileEntity
import com.panjganeh.game.data.local.entity.VipStateEntity
import com.panjganeh.game.ui.theme.LocalFontScale
import com.panjganeh.game.ui.theme.LocalPanjganehTheme
import com.panjganeh.game.ui.theme.PinkTertiary
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.VipCrownGradient
import com.panjganeh.game.ui.theme.VipGold
import com.panjganeh.game.ui.theme.WarmYellow

@Composable
fun ArenaTopBar(
    user: UserProfileEntity?,
    vip: VipStateEntity?,
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    onCoinsClick: () -> Unit = {},
    onTicketsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val fontScale = LocalFontScale.current
    val currentTheme = LocalPanjganehTheme.current
    val isVip = vip?.isVip == true && (vip.expireTimestamp == 0L || vip.expireTimestamp > System.currentTimeMillis())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(38.dp).testTag("topbar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (title != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = (18 * fontScale).sp
                    )
                }
            }
        } else {
            // بخش اطلاعات قهرمان در صفحه اصلی
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onProfileClick() }
                    .padding(4.dp)
                    .testTag("topbar_profile_section")
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isVip) VipCrownGradient
                            else Brush.linearGradient(listOf(currentTheme.primary, currentTheme.secondary))
                        )
                        .border(
                            2.dp,
                            if (isVip) VipGold else currentTheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user?.username ?: "قهرمان پنجگانه",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = (14 * fontScale).sp
                        )
                        if (isVip) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP",
                                tint = VipGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "سطح ${user?.level ?: 1} (${user?.title ?: "تازه وارد"})",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (11 * fontScale).sp
                    )
                }
            }
        }

        // بخش نمایش سکه‌ها و بلیط‌ها با استایل گلس‌مورفیک
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // شمارنده سکه
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, WarmYellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onCoinsClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("coins_counter")
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "سکه",
                    tint = WarmYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${user?.coins ?: 0}",
                    color = WarmYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = (12 * fontScale).sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = WarmYellow,
                    modifier = Modifier.size(12.dp)
                )
            }

            // شمارنده بلیط
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, TurquoiseSecondary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onTicketsClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("tickets_counter")
            ) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = "بلیط",
                    tint = TurquoiseSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${user?.tickets ?: 0}",
                    color = TurquoiseSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = (12 * fontScale).sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = TurquoiseSecondary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
