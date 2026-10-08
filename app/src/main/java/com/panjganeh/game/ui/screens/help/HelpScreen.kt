package com.panjganeh.game.ui.screens.help

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panjganeh.game.PanjganehApplication
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.theme.ArenaBackground
import com.panjganeh.game.ui.theme.ArenaSurface
import com.panjganeh.game.ui.theme.ArenaSurfaceBorder
import com.panjganeh.game.ui.theme.GoldPrimary
import com.panjganeh.game.ui.theme.TextMuted
import com.panjganeh.game.ui.theme.TextPrimary
import com.panjganeh.game.ui.theme.TextSecondary
import com.panjganeh.game.ui.theme.TurquoiseSecondary

@Composable
fun HelpScreen(onNavigateBack: () -> Unit) {
    val app = PanjganehApplication.instance
    val user by app.userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by app.userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ArenaTopBar(
                user = user,
                vip = vip,
                title = "راهنمای بازی",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(ArenaBackground, Color(0xFF081326))))
                .padding(padding),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                HelpHero()
            }
            item { HelpCard("چطور بازی کنم؟", "از صفحه خانه یکی از پنج چالش را انتخاب کن. هر نبرد امتیاز، سکه و XP به همراه دارد.", Icons.Default.SportsKabaddi, TurquoiseSecondary) }
            item { HelpCard("پنج چالش", "تاس، کلمات، حافظه، سنگ‌کاغذقیچی و ساخت جمله؛ هرکدام قوانین و زمان مخصوص خود را دارند.", Icons.Default.Book, GoldPrimary) }
            item { HelpCard("راهنما و جایزه", "برای دریافت سکه و بلیط از جوایز روزانه و ویدیوی جایزه‌دار استفاده کن. VIP تبلیغات را حذف و مزایا را فعال می‌کند.", Icons.Default.Lightbulb, Color(0xFFE8A7FF)) }
            item { HelpCard("پشتیبانی", "اگر مشکلی در بازی یا خرید داشتی، از مسیر پشتیبانی داخل برنامه پیگیری کن.", Icons.Default.HeadsetMic, TurquoiseSecondary) }
        }
    }
}

@Composable
private fun HelpHero() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TurquoiseSecondary.copy(alpha = .65f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, null, tint = TurquoiseSecondary, modifier = Modifier.padding(end = 12.dp))
            Column {
                Text("پنجگانه • راهنمای Arena", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("سریع یاد بگیر، حرفه‌ای بازی کن.", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
}

@Composable
private fun HelpCard(title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface.copy(alpha = .82f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = .35f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null, tint = accent, modifier = Modifier.padding(end = 12.dp))
            Column {
                Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(5.dp))
                Text(body, color = TextSecondary, fontSize = 11.sp, lineHeight = 17.sp)
            }
        }
    }
}
