# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Room database and DAOs
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep Moshi models and adapters
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-dontwarn com.squareup.moshi.**

# Keep Application models and entities
-keep class com.panjganeh.game.data.local.entity.** { *; }
-keep class com.panjganeh.game.billing.** { *; }
-keep class com.panjganeh.game.ads.** { *; }

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

