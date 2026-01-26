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

# 0. CORE
-optimizationpasses 5
-overloadaggressively
-allowaccessmodification
-mergeinterfacesaggressively
#-useuniqueclassmembernames
-dontusemixedcaseclassnames
-ignorewarnings
-dontpreverify
-verbose
-renamesourcefileattribute ""

# 1. MAX SHRINK + STRUCTURE BREAK
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers


# 2. WIPE DEBUG / SOURCE INFO
#-renamesourcefileattribute SourceFile
#-keepattributes !SourceFile,!LineNumberTable,!LocalVariableTable,!LocalVariableTypeTable


# 3. UNREADABLE DICTIONARY
#-obfuscationdictionary dictionary.txt
#-classobfuscationdictionary dictionary.txt
#-packageobfuscationdictionary dictionary.txt


# 4. ANDROID ENTRY POINTS (ONLY REQUIRED)
#-keep class * extends android.app.Application
#-keep class * extends android.app.Activity
#-keep class * extends android.app.Service
#-keep class * extends android.content.BroadcastReceiver
#-keep class * extends android.content.ContentProvider


# 5. KOTLIN (MINIMUM SURVIVAL SET)
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*
#-keep class kotlin.Metadata { *; }


# 6. JETPACK COMPOSE (STRICT)
#-keepclassmembers class * {
#    @androidx.compose.runtime.Composable *;
#}


# 7. REMOVE KOTLIN CHECKS (SIZE + HARDNESS)
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
}


# 8. REMOVE ALL LOGS
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}


# 9. JNI HARDENING
-keepclasseswithmembernames class * {
    native <methods>;
}


# 10. WEBVIEW JS INTERFACE
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}


# 11. ANTI-INFO
-dontnote **
-dontwarn **