package com.panjganeh.game.ui.screens.settings

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.panjganeh.game.PanjganehApplication
import com.panjganeh.game.ai.AiDifficultyLevel
import com.panjganeh.game.data.local.datastore.AppSettings
import com.panjganeh.game.data.local.entity.GameSettingsEntity
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import com.panjganeh.game.ui.components.ArenaTopBar
import com.panjganeh.game.ui.theme.AvailableThemes
import com.panjganeh.game.ui.theme.CreatorCardBorderGradient
import com.panjganeh.game.ui.theme.LocalAppLanguage
import com.panjganeh.game.ui.theme.LocalFontScale
import com.panjganeh.game.ui.theme.LocalPanjganehTheme
import com.panjganeh.game.ui.theme.PanjganehThemePreset
import com.panjganeh.game.ui.theme.PinkTertiary
import com.panjganeh.game.ui.theme.PurplePrimary
import com.panjganeh.game.ui.theme.TurquoiseSecondary
import com.panjganeh.game.ui.theme.WarmYellow
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userRepository: UserRepository,
    gameRepository: GameRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fontScale = LocalFontScale.current
    val currentTheme = LocalPanjganehTheme.current
    val currentLang = LocalAppLanguage.current

    val app = PanjganehApplication.instance
    val settingsDataStore = app.settingsDataStore
    val appSettings by settingsDataStore.appSettingsFlow.collectAsStateWithLifecycle(initialValue = AppSettings())

    val user by userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val vip by userRepository.vipState.collectAsStateWithLifecycle(initialValue = null)
    val roomSettings by gameRepository.settings.collectAsStateWithLifecycle(initialValue = null)

    val currentRoomSettings = roomSettings ?: GameSettingsEntity()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ArenaTopBar(
                user = user,
                vip = vip,
                title = if (currentLang == "en") "Settings" else "تنظیمات پیشرفته",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // ۵.۱: تم رنگی بازی (Theme)
            // ==========================================
            item {
                SectionHeader(
                    title = if (currentLang == "en") "Color Theme" else "تم رنگی بازی (Theme)",
                    icon = Icons.Default.ColorLens,
                    accentColor = currentTheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("theme_selection_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (currentLang == "en") "Choose from 7 energetic themes:" else "از میان ۷ تم شاد و رنگارنگ انتخاب کنید:",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (12 * fontScale).sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(AvailableThemes, key = { it.id }) { theme ->
                                val isSelected = appSettings.themeId == theme.id
                                ThemePill(
                                    preset = theme,
                                    isSelected = isSelected,
                                    lang = currentLang,
                                    onClick = {
                                        scope.launch {
                                            settingsDataStore.setThemeId(theme.id)
                                            Toast.makeText(
                                                context,
                                                if (currentLang == "en") "Theme changed to ${theme.nameEn}" else "تم ${theme.nameFa} اعمال شد",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // ۵.۲: سایز متن (Text Size)
            // ==========================================
            item {
                SectionHeader(
                    title = if (currentLang == "en") "Font Size" else "اندازه متون بازی (Text Size)",
                    icon = Icons.Default.FormatSize,
                    accentColor = currentTheme.secondary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("text_size_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val sizes = listOf(
                            Triple("small", if (currentLang == "en") "Small" else "کوچک", 12.sp),
                            Triple("medium", if (currentLang == "en") "Medium" else "متوسط", 14.sp),
                            Triple("large", if (currentLang == "en") "Large" else "بزرگ", 17.sp)
                        )

                        sizes.forEach { (key, label, previewSize) ->
                            val isSelected = appSettings.textSize == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) currentTheme.primary.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.background
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) currentTheme.primary else Color(0x33FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        scope.launch {
                                            settingsDataStore.setTextSize(key)
                                        }
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) currentTheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = previewSize
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = currentTheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // ۵.۳: حالت روز / شب (Dark/Light Mode)
            // ==========================================
            item {
                SectionHeader(
                    title = if (currentLang == "en") "Display Mode" else "حالت روز و شب (Day / Night)",
                    icon = Icons.Default.Brightness4,
                    accentColor = currentTheme.tertiary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dark_mode_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf(
                            Triple("light", if (currentLang == "en") "Light" else "روز", Icons.Default.LightMode),
                            Triple("dark", if (currentLang == "en") "Dark" else "شب", Icons.Default.DarkMode),
                            Triple("system", if (currentLang == "en") "System" else "خودکار", Icons.Default.Brightness4)
                        )

                        modes.forEach { (key, label, icon) ->
                            val isSelected = appSettings.darkMode == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) currentTheme.tertiary.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.background
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) currentTheme.tertiary else Color(0x33FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        scope.launch {
                                            settingsDataStore.setDarkMode(key)
                                        }
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) currentTheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = label,
                                        color = if (isSelected) currentTheme.tertiary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (12 * fontScale).sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // ۵.۴: تغییر زبان (Language)
            // ==========================================
            item {
                SectionHeader(
                    title = if (currentLang == "en") "Language" else "زبان برنامه (Language)",
                    icon = Icons.Default.Language,
                    accentColor = TurquoiseSecondary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("language_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // فارسی
                        val isFa = appSettings.language == "fa"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isFa) TurquoiseSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                                .border(
                                    width = if (isFa) 2.dp else 1.dp,
                                    color = if (isFa) TurquoiseSecondary else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    scope.launch {
                                        settingsDataStore.setLanguage("fa")
                                        Toast.makeText(context, "زبان به فارسی تغییر یافت", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🇮🇷 فارسی (پیش‌فرض)", color = if (isFa) TurquoiseSecondary else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
                                Text(text = "راست‌چین (RTL)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = (10 * fontScale).sp)
                            }
                        }

                        // انگلیسی
                        val isEn = appSettings.language == "en"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isEn) TurquoiseSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                                .border(
                                    width = if (isEn) 2.dp else 1.dp,
                                    color = if (isEn) TurquoiseSecondary else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    scope.launch {
                                        settingsDataStore.setLanguage("en")
                                        Toast.makeText(context, "Language changed to English", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🇬🇧 English", color = if (isEn) TurquoiseSecondary else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = (13 * fontScale).sp)
                                Text(text = "Left-to-Right (LTR)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = (10 * fontScale).sp)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // سطح دشواری هوش مصنوعی حریف
            // ==========================================
            item {
                SectionHeader(
                    title = if (currentLang == "en") "AI Opponent Difficulty" else "سطح هوش مصنوعی حریف (AI)",
                    icon = Icons.Default.SmartToy,
                    accentColor = currentTheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("ai_difficulty_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AiDifficultyLevel.values().forEach { level ->
                            val isSelected = currentRoomSettings.aiDifficulty == level.titleFa
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) currentTheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) currentTheme.primary else Color(0x22FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        scope.launch {
                                            gameRepository.updateSettings(currentRoomSettings.copy(aiDifficulty = level.titleFa))
                                            Toast.makeText(
                                                context,
                                                if (currentLang == "en") "AI level set to ${level.name}" else "سطح AI روی «${level.titleFa}» تنظیم شد",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (currentLang == "en") level.name else level.titleFa,
                                        color = if (isSelected) currentTheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (13 * fontScale).sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = level.descriptionFa,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = (11 * fontScale).sp
                                    )
                                }

                                Text(
                                    text = "${level.accuracyPercentage}%",
                                    color = if (isSelected) currentTheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (12 * fontScale).sp
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // ۵.۵: تنظیمات صوتی، لرزش و اعلان‌ها
            // ==========================================
            item {
                SectionHeader(
                    title = if (currentLang == "en") "Audio & Haptics" else "صدا، لرزش و اعلان‌ها",
                    icon = Icons.Default.VolumeUp,
                    accentColor = PinkTertiary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingSwitchRowModern(
                            title = if (currentLang == "en") "Sound Effects" else "جلوه‌های صوتی",
                            icon = Icons.Default.VolumeUp,
                            checked = appSettings.soundEffects,
                            tint = currentTheme.primary,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    settingsDataStore.setSoundEffects(checked)
                                    gameRepository.updateSettings(currentRoomSettings.copy(soundEnabled = checked))
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingSwitchRowModern(
                            title = if (currentLang == "en") "Music" else "موسیقی متن بازی",
                            icon = Icons.Default.MusicNote,
                            checked = appSettings.music,
                            tint = currentTheme.secondary,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    settingsDataStore.setMusic(checked)
                                    gameRepository.updateSettings(currentRoomSettings.copy(musicEnabled = checked))
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingSwitchRowModern(
                            title = if (currentLang == "en") "Vibration" else "لرزش (Vibration)",
                            icon = Icons.Default.Vibration,
                            checked = appSettings.vibration,
                            tint = PinkTertiary,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    settingsDataStore.setVibration(checked)
                                    gameRepository.updateSettings(currentRoomSettings.copy(vibrationEnabled = checked))
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingSwitchRowModern(
                            title = if (currentLang == "en") "Notifications" else "اعلان‌های رویدادها و جوایز",
                            icon = Icons.Default.Notifications,
                            checked = appSettings.notifications,
                            tint = WarmYellow,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    settingsDataStore.setNotifications(checked)
                                }
                            }
                        )
                    }
                }
            }

            // ==========================================
            // ۵.۶: بخش «درباره» (About)
            // ==========================================
            item {
                SectionHeader(
                    title = if (currentLang == "en") "About Panjganeh" else "درباره بازی پنجگانه",
                    icon = Icons.Default.Info,
                    accentColor = WarmYellow
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("about_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = 1.5.dp,
                        brush = CreatorCardBorderGradient
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(PurplePrimary, TurquoiseSecondary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (currentLang == "en") "Panjganeh — Version 1.0.0" else "پنجگانه — نسخه ۱.۰.۰",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (14 * fontScale).sp
                                )
                                Text(
                                    text = if (currentLang == "en") "Developer: Seyed Hamid Mousavi Zadeh" else "سازنده: سیدحمید موسوی زاده",
                                    color = TurquoiseSecondary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = (12 * fontScale).sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (currentLang == "en")
                                "Panjganeh is a fast-paced 5-challenge single-player battle against adaptive AI. Five challenges, five battles, one champion. Completely offline without any cloud servers."
                            else
                                "پنج چالش، پنج نبرد، یک قهرمان! بازی پنجگانه یک پلتفرم ۵ چالش هیجان‌انگیز کلمات، حافظه، تاس، سنگ‌کاغذ‌قیچی و جملات در برابر حریف هوش مصنوعی است. بازی کاملاً آفلاین بوده و نیازی به سرور خارجی ندارد.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (12 * fontScale).sp,
                            lineHeight = (18 * fontScale).sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "© 2026 کلیه حقوق متعلق به سیدحمید موسوی زاده است.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = (10 * fontScale).sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color(0x22FFFFFF))
                        Spacer(modifier = Modifier.height(14.dp))

                        // دکمه‌های اشتراک‌گذاری و امتیاز در کافه‌بازار
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "بازی هیجان‌انگیز «پنجگانه» اثر سیدحمید موسوی زاده را تجربه کنید! پنج چالش، پنج نبرد، یک قهرمان:\nhttps://cafebazaar.ir/app/com.panjganeh.game"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری بازی پنجگانه"))
                                },
                                modifier = Modifier.weight(1f).height(44.dp).testTag("share_app_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentTheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentLang == "en") "Share App" else "اشتراک‌گذاری",
                                    fontSize = (12 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=com.panjganeh.game"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cafebazaar.ir/app/com.panjganeh.game"))
                                        context.startActivity(webIntent)
                                    }
                                },
                                modifier = Modifier.weight(1f).height(44.dp).testTag("rate_app_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, TurquoiseSecondary)
                            ) {
                                Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = TurquoiseSecondary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentLang == "en") "Rate on Bazaar" else "امتیاز در بازار",
                                    color = TurquoiseSecondary,
                                    fontSize = (12 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    val fontScale = LocalFontScale.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = (14 * fontScale).sp
        )
    }
}

@Composable
fun ThemePill(
    preset: PanjganehThemePreset,
    isSelected: Boolean,
    lang: String,
    onClick: () -> Unit
) {
    val fontScale = LocalFontScale.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) preset.primary.copy(alpha = 0.2f)
                else Color(0x1AFFFFFF)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) preset.primary else Color(0x33FFFFFF),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("theme_preset_${preset.id}")
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(preset.primary))
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(preset.secondary))
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(preset.tertiary))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (lang == "en") preset.nameEn else preset.nameFa,
            color = if (isSelected) preset.primary else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = (10 * fontScale).sp
        )
    }
}

@Composable
fun SettingSwitchRowModern(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    tint: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val fontScale = LocalFontScale.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = (13 * fontScale).sp,
                fontWeight = FontWeight.Medium
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = tint,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0x33FFFFFF)
            )
        )
    }
}
