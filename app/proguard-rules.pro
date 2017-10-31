# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in G:\sdk/tools/proguard/proguard-android.txt
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

-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
 -keep class com.tamic.novate.** {*;}

 -keep public class * implements com.bumptech.glide.module.GlideModule
 -keep public class * extends com.bumptech.glide.AppGlideModule
 -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
     **[] $VALUES;
     public *;
 }
 -dontwarn com.yalantis.ucrop**
 -keep class com.yalantis.ucrop** { *; }
 -keep interface com.yalantis.ucrop** { *; }

 -dontoptimize
 -dontpreverify
 -keepattributes  EnclosingMethod,Signature
 -dontwarn cn.jpush.**
 -keep class cn.jpush.** { *; }

 -dontwarn cn.jiguang.**
 -keep class cn.jiguang.** { *; }

  -keepclassmembers class ** {
      public void onEvent*(**);
  }

 #========================gson================================
 -dontwarn com.google.**
 -keep class com.google.gson.** {*;}

 #========================protobuf================================
 -keep class com.google.protobuf.** {*;}