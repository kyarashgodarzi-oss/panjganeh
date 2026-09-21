composable(ArenaDestinations.HOME) {
    HomeScreen(
        userRepository = userRepo,
        gameRepository = gameRepo,
        tapsellManager = tapsellManager,    // ← این خط اضافه شد
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
