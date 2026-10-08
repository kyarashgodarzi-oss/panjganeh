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
    val scale = LocalFontScale.current
    val theme = LocalPanjganehTheme.current
    val isVip = vip?.isVip == true && (vip.expireTimestamp == 0L || vip.expireTimestamp > System.currentTimeMillis())
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        theme.primary.copy(alpha = 0.10f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp).testTag("topbar_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = MaterialTheme.colorScheme.onSurface)
                }
                if (title != null) {
                    Spacer(Modifier.width(6.dp))
                    Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.ExtraBold, fontSize = (16 * scale).sp)
                }
            }
        } else {
            Row(
                modifier = Modifier.clip(shape).clickable(onClick = onProfileClick).padding(4.dp).testTag("topbar_profile_section"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape)
                        .background(if (isVip) VipCrownGradient else Brush.linearGradient(listOf(theme.primary, theme.secondary)))
                        .border(2.dp, if (isVip) VipGold else theme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(23.dp))
                }
                Spacer(Modifier.width(9.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user?.username ?: "قهرمان پنجگانه", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.ExtraBold, fontSize = (12 * scale).sp)
                        if (isVip) {
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.WorkspacePremium, "VIP", tint = VipGold, modifier = Modifier.size(15.dp))
                        }
                    }
                    Text("سطح " + (user?.level ?: 1) + " • " + (user?.title ?: "تازه‌وارد"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = (10 * scale).sp)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
            ResourceChip(
                icon = { Icon(Icons.Default.MonetizationOn, null, tint = WarmYellow, modifier = Modifier.size(15.dp)) },
                value = (user?.coins ?: 0).toString(),
                tint = WarmYellow,
                onClick = onCoinsClick,
                tag = "coins_counter"
            )
            ResourceChip(
                icon = { Icon(Icons.Default.ConfirmationNumber, null, tint = theme.secondary, modifier = Modifier.size(15.dp)) },
                value = (user?.tickets ?: 0).toString(),
                tint = theme.secondary,
                onClick = onTicketsClick,
                tag = "tickets_counter"
            )
        }
    }
}

@Composable
private fun ResourceChip(
    icon: @Composable () -> Unit,
    value: String,
    tint: Color,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(14.dp))
            .background(tint.copy(alpha = 0.10f))
            .border(1.dp, tint.copy(alpha = 0.32f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(4.dp))
        Text(value, color = tint, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        Spacer(Modifier.width(2.dp))
        Icon(Icons.Default.Add, null, tint = tint, modifier = Modifier.size(11.dp))
    }
}
