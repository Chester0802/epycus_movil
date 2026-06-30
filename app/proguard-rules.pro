# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation class * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# Gson
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class es.epycus.app.model.** { *; }
-keepclassmembers class es.epycus.app.model.dto.** { *; }
-keepclassmembers class es.epycus.app.model.entidades.** { *; }

# Room — keep generated _Impl classes and constructors used by reflection
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers @androidx.room.Entity class * { *; }
-keep class **._Impl { <init>(); }
-keep class es.epycus.app.data.local.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Glide
-keep class com.bumptech.glide.** { *; }
-keep class * extends com.bumptech.glide.module.AppGlideModule
-keep class * extends com.bumptech.glide.module.LibraryGlideModule

# Fragment/AppCompat — evitar ClassCastException por R8 full mode
-keep class androidx.fragment.app.** { *; }
-keep class androidx.appcompat.app.** { *; }
-keep class androidx.activity.** { *; }

# ViewBinding
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static * inflate(...);
}

# SignalR (usa reflexión + Gson/RxJava; slf4j es dependencia opcional de logging)
-keep class com.microsoft.signalr.** { *; }
-dontwarn com.microsoft.signalr.**
-dontwarn org.slf4j.**

# WorkManager: los Worker se instancian por reflexión (incluido SyncWorker)
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# Keep BuildConfig
-keep class es.epycus.app.BuildConfig { *; }