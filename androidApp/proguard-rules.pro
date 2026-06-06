-keep class * extends androidx.room.RoomDatabase { <init>(); }

# Keep Screen sealed interface and its implementations for navigation
-keep class dev.seabat.ramennote.ui.navigation.Screen { *; }
-keep class * implements dev.seabat.ramennote.ui.navigation.Screen { *; }
-keep class * extends dev.seabat.ramennote.ui.navigation.Screen { *; }

# This is generated automatically by the Android Gradle plugin.
-dontwarn io.ktor.client.plugins.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.plugins.HttpTimeout$Plugin
-dontwarn io.ktor.client.plugins.HttpTimeout
-dontwarn io.ktor.client.plugins.contentnegotiation.ContentNegotiation$Config
-dontwarn io.ktor.client.plugins.contentnegotiation.ContentNegotiation$Plugin
-dontwarn io.ktor.client.plugins.contentnegotiation.ContentNegotiation