package com.panjganeh.game.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.panjganeh.game.PanjganehApplication
import com.panjganeh.game.challenges.dice.DiceBattleScreen
import com.panjganeh.game.challenges.dice.DiceBattleViewModel
import com.panjganeh.game.challenges.memory.MemoryCardsScreen
import com.panjganeh.game.challenges.memory.MemoryCardsViewModel
import com.panjganeh.game.challenges.rps.RockPaperScissorsScreen
import com.panjganeh.game.challenges.rps.RockPaperScissorsViewModel
import com.panjganeh.game.challenges.sentence.SentenceBuilderScreen
import com.panjganeh.game.challenges.sentence.SentenceBuilderViewModel
import com.panjganeh.game.challenges.word.WordBattleScreen
import com.panjganeh.game.challenges.word.WordBattleViewModel
import com.panjganeh.game.ui.screens.help.HelpScreen
import com.panjganeh.game.ui.screens.home.HomeScreen
import com.panjganeh.game.ui.screens.leaderboard.LeaderboardScreen
import com.panjganeh.game.ui.screens.modes.ModeSelectionScreen
import com.panjganeh.game.ui.screens.online.OnlineConnectionScreen
import com.panjganeh.game.ui.screens.profile.ProfileScreen
import com.panjganeh.game.ui.screens.rewards.DailyRewardsScreen
import com.panjganeh.game.ui.screens.settings.SettingsScreen
import com.panjganeh.game.ui.screens.shop.ShopScreen
import com.panjganeh.game.ui.screens.splash.SplashScreen

object ArenaDestinations {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val SHOP = "shop"
    const val REWARDS = "rewards"
    const val LEADERBOARD = "leaderboard"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val MODES = "modes"
    const val ONLINE = "online"
    const val BATTLE_WORD = "battle_word"
    const val BATTLE_MEMORY = "battle_memory"
    const val BATTLE_DICE = "battle_dice"
    const val BATTLE_RPS = "battle_rps"
    const val BATTLE_SENTENCE = "battle_sentence"
}

@Composable
fun ArenaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val app = PanjganehApplication.instance
    val userRepo = app.userRepository
    val gameRepo = app.gameRepository
    val billingManager = app.billingManager
    val tapsellManager = app.tapsellManager
    val settingsDataStore = app.settingsDataStore

    val settings by gameRepo.settings.collectAsStateWithLifecycle(initialValue = null)
    val aiDifficulty = settings?.aiDifficulty ?: "متوسط"

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showReferenceBottomBar = currentRoute in setOf(
        ArenaDestinations.SHOP,
        ArenaDestinations.REWARDS,
        ArenaDestinations.LEADERBOARD,
        ArenaDestinations.PROFILE,
        ArenaDestinations.SETTINGS,
        ArenaDestinations.HELP
    )

    Scaffold(
        containerColor = Color(0xFF050A14),
        bottomBar = {
            if (showReferenceBottomBar) {
                ReferenceBottomNavigation(
                    currentRoute = currentRoute,
                    onHome = { navController.navigate(ArenaDestinations.HOME) { popUpTo(ArenaDestinations.HOME) { inclusive = false } } },
                    onProfile = { navController.navigate(ArenaDestinations.PROFILE) },
                    onLeaderboard = { navController.navigate(ArenaDestinations.LEADERBOARD) },
                    onGame = { navController.navigate(ArenaDestinations.MODES) },
                    onShop = { navController.navigate(ArenaDestinations.SHOP) }
                )
            }
        }
    ) { rootPadding ->
    NavHost(
        navController = navController,
        startDestination = ArenaDestinations.SPLASH,
        modifier = Modifier.fillMaxSize().padding(bottom = rootPadding.calculateBottomPadding())
    ) {
        composable(ArenaDestinations.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(ArenaDestinations.HOME) {
                        popUpTo(ArenaDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(ArenaDestinations.HOME) {
            HomeScreen(
                userRepository = userRepo,
                gameRepository = gameRepo,
                tapsellManager = tapsellManager,
                settingsDataStore = settingsDataStore,
                onStartChallenge = { challengeId ->
                    val destination = when (challengeId) {
                        "word" -> ArenaDestinations.BATTLE_WORD
                        "memory" -> ArenaDestinations.BATTLE_MEMORY
                        "dice" -> ArenaDestinations.BATTLE_DICE
                        "rps" -> ArenaDestinations.BATTLE_RPS
                        "sentence" -> ArenaDestinations.BATTLE_SENTENCE
                        else -> ArenaDestinations.BATTLE_WORD
                    }
                    navController.navigate(destination)
                },
                onNavigateToShop = { navController.navigate(ArenaDestinations.SHOP) },
                onNavigateToRewards = { navController.navigate(ArenaDestinations.REWARDS) },
                onNavigateToLeaderboard = { navController.navigate(ArenaDestinations.LEADERBOARD) },
                onNavigateToProfile = { navController.navigate(ArenaDestinations.PROFILE) },
                onNavigateToSettings = { navController.navigate(ArenaDestinations.SETTINGS) },
                onNavigateToOnline = { navController.navigate(ArenaDestinations.SHOP) },
                onNavigateToModes = { navController.navigate(ArenaDestinations.MODES) }
            )
        }

        composable(ArenaDestinations.MODES) {
            ModeSelectionScreen(
                userRepository = userRepo,
                gameRepository = gameRepo,
                onStartChallenge = { challengeId ->
                    val destination = when (challengeId) {
                        "word" -> ArenaDestinations.BATTLE_WORD
                        "memory" -> ArenaDestinations.BATTLE_MEMORY
                        "dice" -> ArenaDestinations.BATTLE_DICE
                        "rps" -> ArenaDestinations.BATTLE_RPS
                        "sentence" -> ArenaDestinations.BATTLE_SENTENCE
                        else -> ArenaDestinations.BATTLE_WORD
                    }
                    navController.navigate(destination)
                },
                onNavigateToOnline = { navController.navigate(ArenaDestinations.SHOP) },
                onNavigateToRewards = { navController.navigate(ArenaDestinations.REWARDS) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.ONLINE) {
            OnlineConnectionScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ArenaDestinations.SHOP) {
            ShopScreen(
                userRepository = userRepo,
                billingManager = billingManager,
                tapsellManager = tapsellManager,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.REWARDS) {
            DailyRewardsScreen(
                userRepository = userRepo,
                tapsellManager = tapsellManager,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.LEADERBOARD) {
            LeaderboardScreen(
                userRepository = userRepo,
                tapsellManager = tapsellManager,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.PROFILE) {
            ProfileScreen(
                userRepository = userRepo,
                gameRepository = gameRepo,
                tapsellManager = tapsellManager,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(ArenaDestinations.SETTINGS) }
            )
        }

        composable(ArenaDestinations.SETTINGS) {
            SettingsScreen(
                userRepository = userRepo,
                gameRepository = gameRepo,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHelp = { navController.navigate(ArenaDestinations.HELP) }
            )
        }

        composable(ArenaDestinations.HELP) {
            HelpScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // مسابقات پنج‌گانه (همه با tapsellManager)
        // ═══════════════════════════════════════════════════════════════
        composable(ArenaDestinations.BATTLE_WORD) {
            val vm: WordBattleViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return WordBattleViewModel(gameRepo, userRepo) as T
                }
            })
            WordBattleScreen(
                viewModel = vm,
                tapsellManager = tapsellManager,
                difficulty = aiDifficulty,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.BATTLE_MEMORY) {
            val vm: MemoryCardsViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MemoryCardsViewModel(gameRepo, userRepo) as T
                }
            })
            MemoryCardsScreen(
                viewModel = vm,
                tapsellManager = tapsellManager,
                difficulty = aiDifficulty,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.BATTLE_DICE) {
            val vm: DiceBattleViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return DiceBattleViewModel(gameRepo, userRepo) as T
                }
            })
            DiceBattleScreen(
                viewModel = vm,
                tapsellManager = tapsellManager,
                difficulty = aiDifficulty,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.BATTLE_RPS) {
            val vm: RockPaperScissorsViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return RockPaperScissorsViewModel(gameRepo, userRepo) as T
                }
            })
            RockPaperScissorsScreen(
                viewModel = vm,
                tapsellManager = tapsellManager,
                difficulty = aiDifficulty,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.BATTLE_SENTENCE) {
            val vm: SentenceBuilderViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SentenceBuilderViewModel(gameRepo, userRepo) as T
                }
            })
            SentenceBuilderScreen(
                viewModel = vm,
                tapsellManager = tapsellManager,
                difficulty = aiDifficulty,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
    }
}

@Composable
private fun ReferenceBottomNavigation(
    currentRoute: String?,
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onLeaderboard: () -> Unit,
    onGame: () -> Unit,
    onShop: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth()
            .background(Color(0xFF030812))
            .border(1.dp, Color(0xFF12233B))
            .padding(horizontal = 3.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReferenceNavItem("پروفایل", Icons.Default.Person, currentRoute == ArenaDestinations.PROFILE, onProfile)
        ReferenceNavItem("رتبه‌بندی", Icons.Default.EmojiEvents, currentRoute == ArenaDestinations.LEADERBOARD, onLeaderboard)
        ReferenceNavItem("مسابقه", Icons.Default.Gamepad, currentRoute == ArenaDestinations.MODES, onGame)
        ReferenceNavItem("فروشگاه", Icons.Default.ShoppingCart, currentRoute == ArenaDestinations.SHOP, onShop)
        ReferenceNavItem("خانه", Icons.Default.Home, currentRoute == ArenaDestinations.HOME, onHome)
    }
}

@Composable
private fun ReferenceNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        Modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(horizontal = 7.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = if (selected) Color(0xFFFFC928) else Color(0xFF8A9AAF), modifier = Modifier.size(21.dp))
        Text(label, color = if (selected) Color(0xFFFFC928) else Color(0xFF8A9AAF), fontSize = 8.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
