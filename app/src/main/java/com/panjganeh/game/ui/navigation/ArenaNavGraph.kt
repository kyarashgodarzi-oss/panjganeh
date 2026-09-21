package com.panjganeh.game.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.panjganeh.game.ui.screens.home.HomeScreen
import com.panjganeh.game.ui.screens.leaderboard.LeaderboardScreen
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

    val settings by gameRepo.settings.collectAsStateWithLifecycle(initialValue = null)
    val aiDifficulty = settings?.aiDifficulty ?: "متوسط"

    NavHost(
        navController = navController,
        startDestination = ArenaDestinations.SPLASH,
        modifier = Modifier.fillMaxSize()
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
                onNavigateToSettings = { navController.navigate(ArenaDestinations.SETTINGS) }
            )
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
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.LEADERBOARD) {
            LeaderboardScreen(
                userRepository = userRepo,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.PROFILE) {
            ProfileScreen(
                userRepository = userRepo,
                gameRepository = gameRepo,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ArenaDestinations.SETTINGS) {
            SettingsScreen(
                userRepository = userRepo,
                gameRepository = gameRepo,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // مسابقات پنج‌گانه
        composable(ArenaDestinations.BATTLE_WORD) {
            val vm: WordBattleViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return WordBattleViewModel(gameRepo, userRepo) as T
                }
            })
            WordBattleScreen(
                viewModel = vm,
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
                difficulty = aiDifficulty,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
