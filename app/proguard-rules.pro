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


# ---------------------------------------------------------
# 1. TOTAL METADATA STRIPPING
# ---------------------------------------------------------
# Forcefully remove source file names and line numbers to hide code structure
-renamesourcefileattribute ''
-keepattributes !SourceFile,!LineNumberTable

# Remove local variable tables (Highly recommended for security)
-keepattributes !LocalVariableTable,!LocalVariableTypeTable

# ---------------------------------------------------------
# 2. AGGRESSIVE OBFUSCATION & SHRINKING
# ---------------------------------------------------------
# Repackage all classes into a single root to confuse decompilers
-repackageclasses ''
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively

# Use optimization passes (3 is optimal for R8/AGP 9.0)
-optimizationpasses 3

# ---------------------------------------------------------
# 3. KOTLIN & COMPOSE ESSENTIALS (Required for the app to run)
# ---------------------------------------------------------
# We keep only the absolute minimum required for Kotlin/Compose to not crash
-keepattributes Signature,EnclosingMethod,InnerClasses,*Annotation*

-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
    @androidx.compose.runtime.ReadOnlyComposable *;
}

# Keep native methods for JNI security
-keepclasseswithmembernames class * {
    native <methods>;
}

# Hardening for WebView if used
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ---------------------------------------------------------
# 4. CLEANUP
# ---------------------------------------------------------
-dontnote **
-dontwarn **