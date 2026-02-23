# =====================================================================
# JellyDrink — ProGuard Rules
# =====================================================================

# ---------- Room ----------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.paging.**
# Mantieni le entity del progetto (usate da Room come data class)
-keep class com.jellydrink.app.data.db.entity.** { *; }
# Mantieni i DTO (DailySummary, BadgeWithStatus, ecc.)
-keep class com.jellydrink.app.data.db.dao.DailySummary { *; }
-keep class com.jellydrink.app.data.repository.** { *; }

# ---------- Hilt ----------
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.hilt.InstallIn class *
-keep @javax.inject.Singleton class *

# ---------- Kotlin ----------
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Lazy { *; }

# ---------- Coroutines ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ---------- Jetpack Compose ----------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ---------- DataStore ----------
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ---------- WorkManager ----------
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# ---------- Navigation ----------
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ---------- AppCompat ----------
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**

# ---------- Widget / RemoteViews ----------
-keep class * extends android.appwidget.AppWidgetProvider

# ---------- Generali Android ----------
# Mantieni le classi con @Keep
-keep @androidx.annotation.Keep class * { *; }
# Non offuscare i nomi delle classi referenziate nelle stringhe (reflection)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
