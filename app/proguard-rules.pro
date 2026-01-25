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


## =========================================================
# ADVANCED HARDENING & AGGRESSIVE OBFUSCATION
# =========================================================

# ---------------------------------------------------------
# 1. THE "TOTAL WIPE": STRIP ALL TRACES
# ---------------------------------------------------------
# Removes every bit of debug info. Stack traces will be unreadable.
-renamesourcefileattribute ''
-keepattributes !SourceFile,!LineNumberTable
-keepattributes !LocalVariableTable,!LocalVariableTypeTable

-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt

# ---------------------------------------------------------
# 2. DICTIONARY OBFUSCATION (The "Alien Code" look)
# ---------------------------------------------------------
# This makes your classes and methods look like 'a', 'b', 'I1l', etc.
-useuniqueclassmembernames
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-mergeinterfacesaggressively

# ---------------------------------------------------------
# 3. OPTIMIZATION & PERFORMANCE HARDENING
# ---------------------------------------------------------
-optimizationpasses 5
-dontpreverify

# Remove all Android Logs (Security & Size improvement)
# This prevents hackers from reading your log outputs.
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# ---------------------------------------------------------
# 4. KOTLIN & COMPOSE PROTECTION (STRICT MINIMUM)
# ---------------------------------------------------------
# Compose needs these to run, but we strip everything else.
-keepattributes Signature,EnclosingMethod,InnerClasses,*Annotation*

-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
    @androidx.compose.runtime.ReadOnlyComposable *;
}

# Keep Kotlin Metadata but minimize its contents
-keep class kotlin.Metadata { *; }

# Strip Kotlin assertions and null checks (Reduces size)
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
}

# ---------------------------------------------------------
# 5. ENTRY POINT PROTECTION (CRITICAL)
# ---------------------------------------------------------
# Only keep what Android OS needs to start the app. 
# Everything else gets renamed or deleted.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# ---------------------------------------------------------
# 6. SECURITY & JNI (NATIVE) HARDENING
# ---------------------------------------------------------
-keepclasseswithmembernames class * {
    native <methods>;
}

# WebView security (if you ever use it)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Disable all ProGuard notes and warnings to hide the process
-dontnote **
-dontwarn **