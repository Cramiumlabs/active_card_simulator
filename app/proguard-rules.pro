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

-keep class com.corbado.passkeys_android.** { *; }
-keep class com.corbado.** { *; }
-keepclassmembers class com.corbado.passkeys_android.** { *; }
-keepclassmembers class com.corbado.** { *; }
-keep class meme.mosu.wallet.** { *; }
-keepclassmembers class meme.mosu.wallet.** { *; }
-keep class com.cramium.sdk.** { *; }
-keepclassmembers class com.cramium.sdk.** { *; }
-keep class com.example.cramium_flutter_sdk.** { *; }
-keepclassmembers class com.example.cramium_flutter_sdk.** { *; }
