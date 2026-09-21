package com.panjganeh.game

import android.app.Application
import com.panjganeh.game.ads.TapsellManager
import com.panjganeh.game.billing.BazaarBillingManager
import com.panjganeh.game.data.local.AppDatabase
import com.panjganeh.game.data.local.datastore.AppSettingsDataStore
import com.panjganeh.game.data.local.initDefaults
import com.panjganeh.game.data.repository.GameRepository
import com.panjganeh.game.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * کلاس اپلیکیشن اصلی بازی پنجگانه
 * مدیریت تزریق وابستگی‌های سراسری و رجیستری مخازن داده
 */
class PanjganehApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var userRepository: UserRepository
        private set

    lateinit var gameRepository: GameRepository
        private set

    lateinit var billingManager: BazaarBillingManager
        private set

    lateinit var tapsellManager: TapsellManager
        private set

    lateinit var settingsDataStore: AppSettingsDataStore
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        userRepository = UserRepository(database)
        gameRepository = GameRepository(this, database)
        billingManager = BazaarBillingManager(this, userRepository)
        tapsellManager = TapsellManager(this, userRepository)
        settingsDataStore = AppSettingsDataStore(this)

        // اطمینان از ساخت داده‌های اولیه به صورت ناهمگام
        CoroutineScope(Dispatchers.IO).launch {
            initDefaults(database)
        }
    }

    companion object {
        lateinit var instance: PanjganehApplication
            private set
    }
}
