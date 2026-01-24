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
# 1. AGGRESSIVE OPTIMIZATION & CODE HARDENING
# ---------------------------------------------------------
# Increase optimization passes to deeply restructure bytecode
-optimizationpasses 10
-allowaccessmodification
-mergeinterfacesaggressively
-repackageclasses ''
-overloadaggressively
-dontpreverify

# ---------------------------------------------------------
# 2. TOTAL DEBUG INFORMATION WIPE
# ---------------------------------------------------------
# [cite_start]Remove all metadata that hackers use to understand your code [cite: 4]
# -dontattributes SourceFile,LineNumberTable,Signature,EnclosingMethod,InnerClasses,LocalVariableTable,LocalVariableTypeTable
-renamesourcefileattribute ''

# ---------------------------------------------------------
# 3. ADVANCED DICTIONARY OBFUSCATION
# ---------------------------------------------------------
# Use "unreadable" characters for class/member names.
# You can provide a custom text file with special characters (e.g., ilI1)
-useuniqueclassmembernames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# ---------------------------------------------------------
# 4. STRING & REFLECTION PROTECTION
# ---------------------------------------------------------
# Only keep what is absolutely necessary for the Android OS to run the app
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.view.View

# Keep annotations only if they are used by essential libraries (like Retrofit/Room)
-keepattributes *Annotation*

# Reflection protection
-keepattributes *Annotation*, EnclosingMethod, InnerClasses

# ---------------------------------------------------------
# 5. NATIVE (JNI) SECURITY & API KEY PROTECTION
# ---------------------------------------------------------
# Protects the interface between Java and C++. 
# Essential if you use XOR encryption in C++ to hide API keys.
-keepclasseswithmembernames class * {
    native <methods>;
}

# ---------------------------------------------------------
# [cite_start]6. WEBVIEW & JAVASCRIPT HARDENING [cite: 2, 3]
# ---------------------------------------------------------
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ---------------------------------------------------------
# 7. ANTI-FRIDA & ANTI-TAMPER RULES
# ---------------------------------------------------------
# This makes it harder for tools to "hook" into your methods
-dontnote **
-dontwarn **