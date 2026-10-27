# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# ===================================================================
# Reglas de seguridad para Release (R8 / ProGuard)
# ===================================================================

# Preservar todos los modelos DTO de red para que Retrofit/Gson mapeen el JSON de la API
-keep class com.ispc.todostock.network.** { *; }

# Preservar anotaciones, firmas y métodos de Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-dontwarn okhttp3.**
-dontwarn okio.**
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Reglas para Google Tink y EncryptedSharedPreferences (SesionManager)
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.errorprone.annotations.**
