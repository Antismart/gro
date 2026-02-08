# ============================================================
# Grō — ProGuard / R8 Rules
# ============================================================

# Preserve line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- kotlinx.serialization ----
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.gro.**$$serializer { *; }
-keepclassmembers class com.example.gro.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.gro.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ---- Ktor ----
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ---- Sol4k ----
-keep class org.sol4k.** { *; }
-dontwarn org.sol4k.**
-keep class org.bitcoinj.** { *; }
-dontwarn org.bitcoinj.**

# ---- Mobile Wallet Adapter ----
-keep class com.solana.mobilewalletadapter.** { *; }
-dontwarn com.solana.mobilewalletadapter.**

# ---- Room ----
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ---- Hilt ----
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ---- Compose ----
-dontwarn androidx.compose.**

# ---- DataStore ----
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { *; }

# ---- Security Crypto ----
-keep class androidx.security.crypto.** { *; }

# ---- Coil ----
-dontwarn coil.**

# ---- Lottie ----
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# ---- Kotlin ----
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
