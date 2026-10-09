package com.panjganeh.game.ui.screens.online

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panjganeh.game.R
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.WarmYellow

@Composable
fun OnlineConnectionScreen(
    onNavigateBack: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(ArenaBackground)) {
        Image(
            painter = painterResource(R.drawable.arena_clash_icon_1789948798958),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.20f
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B1930).copy(alpha = .76f),
                        Color(0xFF07101E).copy(alpha = .90f),
                        ArenaBackground.copy(alpha = .98f)
                    )
                )
            )
        )
        Column(
            Modifier.fillMaxSize().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(42.dp).clip(CircleShape)
                        .background(ArenaSurface)
                        .border(1.dp, TurquoiseSecondary.copy(alpha = .35f), CircleShape)
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = TextPrimary)
                }
                Column(Modifier.weight(1f).padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("اتصال آنلاین", color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text("پنجگانه • ONLINE ARENA", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                }
                Box(Modifier.size(42.dp))
            }

            Spacer(Modifier.height(34.dp))

            Box(
                Modifier.size(128.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(TurquoiseSecondary.copy(alpha = .28f), PurplePrimary.copy(alpha = .08f))))
                    .border(2.dp, TurquoiseSecondary.copy(alpha = .55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Wifi, null, tint = TurquoiseSecondary, modifier = Modifier.size(58.dp))
            }

            Text("آماده‌ی ورود به میدان آنلاین؟", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 20.dp))
            Text("دوستت را دعوت کن یا وارد صف رقابت شو.", color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 7.dp))

            Column(
                Modifier.fillMaxWidth().padding(top = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OnlineOption(Icons.Default.PersonAdd, "دعوت از دوست", "یک نبرد خصوصی با کد اتاق بساز.")
                OnlineOption(Icons.Default.Public, "مسابقه سریع", "به نزدیک‌ترین رقیب موجود متصل شو.")
                OnlineOption(Icons.Default.Cloud, "همگام‌سازی حساب", "پیشرفت و جوایزت را در حساب نگه دار.")
            }

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 10.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarmYellow)
            ) {
                Icon(Icons.Default.Wifi, null, tint = Color(0xFF241407))
                Text("جستجوی حریف", color = Color(0xFF241407), fontWeight = FontWeight.Black, fontSize = 15.sp, modifier = Modifier.padding(start = 7.dp))
            }

            Text("اتصال آنلاین در این نسخه به زیرساخت سرویس آنلاین بازی وابسته است.", color = TextMuted, fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun OnlineOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp))
            .background(ArenaSurface.copy(alpha = .78f))
            .border(1.dp, TurquoiseSecondary.copy(alpha = .18f), RoundedCornerShape(17.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(TurquoiseSecondary.copy(alpha = .10f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = TurquoiseSecondary, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f).padding(start = 11.dp)) {
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
        }
    }
}
