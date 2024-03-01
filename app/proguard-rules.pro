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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Save mapping between original and obfuscated class names
# for stack traces decoding in the future
-printmapping ./build/app-r8-mapping.txt

-optimizationpasses 5

# Print the resulting configuration
-printconfiguration ./build/app-full-r8-config.txt

-printusage ./build/app-full-r8-usage.txt
-printseeds ./build/app-full-r8-seeds.txt

# maybe this will keep the NetworkResponseAdapter library working
#-keep class com.haroldadmin.cnradapter.** { *; }

-verbose
#generated
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi

-dontobfuscate
 -keep class ua.leonidius.beatinspector.datasources.network.dto.** {*;}