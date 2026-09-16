# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# حماية مكتبات Firebase أثناء التحويل والسحب
-keepattributes *Annotation*,Signature,InnerClasses
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# تجنب أخطاء بناء الموارد السريعة
-dontwarn okio.**
-dontwarn javax.annotation.**
