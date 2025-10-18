-keep class * extends androidx.room.RoomDatabase { <init>(); }

# Keep Screen sealed interface and its implementations for navigation
-keep class dev.seabat.ramennote.ui.navigation.Screen { *; }
-keep class * implements dev.seabat.ramennote.ui.navigation.Screen { *; }
-keep class * extends dev.seabat.ramennote.ui.navigation.Screen { *; }