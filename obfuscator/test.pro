-injars      /Users/lgitzing/Library/Android/sdk/tools/proguard6/bin/locker-fe666e209e094968d3178ecf0cf817164c26d5501ed3cd9a80da786a4a3f3dc4-dex2jar.jar
-outjars     ./classes-out.jar
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-12
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-13
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-14
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-15
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-16
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-17
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-18
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-19
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-20
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-21
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-22
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-23
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-24
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-25
#-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-26
-libraryjars /Users/lgitzing/Library/Android/sdk/platforms/android-27

-dontwarn android.support.**
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-flattenpackagehierarchy
-verbose
-obfuscationdictionary method-dictionary.txt
-packageobfuscationdictionary package-dictionary.txt
-classobfuscationdictionary class-dictionary.txt
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizations !method/inlining/*
-optimizationpasses 1
#-optimizationpasses 5
-allowaccessmodification
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** i(...);
public static *** v(...);
}
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment

-keep public class * extends android.view.View {
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet); public <init>(android.content.Context, android.util.AttributeSet, int);
public void set*(...);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet,int);
}
-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
public static <fields>;
}

#-applymapping mapping.txt
-keep class android.support.v4.app.** {*;}
-keep interface android.support.v4.app.** {*;}

#The support library contains references to newer platform versions.
#Don't warn about those in case this app is linking against an older
#platform version. We know about them, and they are safe.
-keepattributes JavascriptInterface
-keepattributes *Annotation*s
