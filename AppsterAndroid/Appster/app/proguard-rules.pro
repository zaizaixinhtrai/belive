# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/thanhbc/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#Use 5 step of optimization
#-optimizationpasses 5

#When not preverifing in a case-insensitive filing system, such as Windows. This tool will unpack your processed jars,(if using windows you should then use):
-dontusemixedcaseclassnames

#Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting
-dontskipnonpubliclibraryclasses

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify

-dontwarn android.support.**

#Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose

-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#Uncomment if using annotations to keep them.
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature

#Keep classes that are referenced on the AndroidManifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
#Compatibility library
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment


#To maintain custom components names that are used on layouts XML.
#Uncomment if having any problem with the approach below
#-keep public class custom.components.package.and.name.**

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
 -keepclassmembers public class * extends android.view.View {
  void set*(***);
  *** get*();
}

#To remove debug logs:
-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
     public static *** i(...);
      public static *** e(...);
}

#To avoid changing names of methods invoked on layout's onClick.
# Uncomment and add specific method names if using onClick on layouts
#-keepclassmembers class * {
# public void onClickButton(android.view.View);
#}

#Maintain java native methods
-keepclassmembers class * {
    native <methods>;
}

#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

#Uncomment if using Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Retrolambda
-dontwarn java.lang.invoke.*

#javacv
-keep class com.googlecode.javacv.**{ *; }
-keepclassmembers class com.googlecode.javacv.** {
    <methods>;
}
-keep class com.googlecode.javacpp.**{ *; }
-keepclassmembers class com.googlecode.javacpp.** {
    <methods>;
}

-keepattributes *Annotation*

-dontwarn com.googlecode.javacv.**, com.googlecode.javacpp.**
-dontnote com.googlecode.javacv.**, com.googlecode.javacpp.**

#mp4parser
-keep class * implements com.coremedia.iso.boxes.Box { *; }
-dontwarn com.coremedia.iso.boxes.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.mjpeg.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.ttml.**

#FloatMath deprecated on API 23
-dontwarn com.handmark.pulltorefresh.library.**
-dontwarn uk.co.senab.photoview.**

#keep models' field name
#-keep class com.appster.DataModel.*{*;}
-keep class com.appster.models.*{*;}
#-keep class com.appster.webservice.response_models.*{*;}
#-keep class com.appster.webservice.request_models.*{*;}
#-keep class com.appster.webservice.response.*{*;}
-keep class com.appster.message.*{*;}
-keep class com.appster.newsfeed.*{*;}
-keep class com.appster.sendgift.*{*;}
-keep class com.appster.comments.*{*;}
-keep class com.appster.bundle.*{*;}

#vxg
-keep class veg.mediaplayer.sdk.**
-keep class veg.mediaplayer.sdk.**{*;}

#debug class
-keep class com.com.appster.webservice.request_models.ApiDebugModel

-keep class org.wysaid.nativePort.CGENativeLibrary
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.**

# Kotlin
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

#dagger2
-dontwarn com.google.errorprone.annotations.*
# If you want to get rid of null checks at runtime you may use the following rule:
#-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
#    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
#}

-keep class com.google.android.gms.measurement.AppMeasurement { *; }
-keep class com.google.android.gms.measurement.AppMeasurement$OnEventListener { *; }