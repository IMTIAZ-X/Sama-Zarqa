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
-optimizationpasses 3
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
#-renamesourcefileattribute ''

# ---------------------------------------------------------
# THE SECRET WEAPON: CUSTOM DICTIONARY
# ---------------------------------------------------------
# This makes your code look like: val l1ll1 = "..." 
# Create a file named 'mapping.txt' with characters like i, l, 1, I
# -obfuscationdictionary mapping.txt
# -classobfuscationdictionary mapping.txt
# -packageobfuscationdictionary mapping.txt

# ---------------------------------------------------------
# 3. METADATA STRIPPING (DO NOT REMOVE SIGNATURE)
# ---------------------------------------------------------
# We strip line numbers but KEEP Signatures and InnerClasses 
# because Kotlin Reflection and Compose need them to work.
-keepattributes Signature, EnclosingMethod, InnerClasses, *Annotation*
-renamesourcefileattribute ''
-dontskipnonpubliclibraryclasses

# ---------------------------------------------------------
#  ADVANCED DICTIONARY OBFUSCATION
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
#-keepattributes *Annotation*

# Reflection protection
#-keepattributes *Annotation*, EnclosingMethod, InnerClasses

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
# 4. JETPACK COMPOSE & KOTLIN SPECIFIC
# ---------------------------------------------------------
# Since you use Compose, we must protect the Composable functions
#-keepclassmembers class * {
#    @androidx.compose.runtime.Composable *;
#    @androidx.compose.runtime.ReadOnlyComposable *;
#}

# Keep Kotlin standard library metadata (important for stability)
#-keep class kotlin.Metadata { *; }

# ---------------------------------------------------------
# 7. ANTI-FRIDA & ANTI-TAMPER RULES
# ---------------------------------------------------------
# This makes it harder for tools to "hook" into your methods
-dontnote **
-dontwarn **


# Removes every bit of debug info. Stack traces will be unreadable.
#-renamesourcefileattribute ''
#-keepattributes !SourceFile,!LineNumberTable
#-keepattributes !LocalVariableTable,!LocalVariableTypeTable
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt
# This makes your classes and methods look like 'a', 'b', 'I1l', etc.
#-useuniqueclassmembernames
#-overloadaggressively
#-repackageclasses ''
#-allowaccessmodification
#-mergeinterfacesaggressively


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
#-keepattributes Signature,EnclosingMethod,InnerClasses,*Annotation*

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
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider