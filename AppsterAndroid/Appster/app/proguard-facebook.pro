# Facebook 3.2

-keep class com.facebook.** { *; }
-keepattributes Signature
#-keep class com.facebook.fbui.textlayoutbuilder.proxy.StaticLayoutProxy
#-dontwarn com.facebook.fbui.textlayoutbuilder.proxy.StaticLayoutProxy{ *; }
-dontwarn android.text.StaticLayout