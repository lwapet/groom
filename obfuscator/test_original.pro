-injars      /Users/lgitzing/Library/Android/sdk/tools/proguard6/bin/locker-fe666e209e094968d3178ecf0cf817164c26d5501ed3cd9a80da786a4a3f3dc4-dex2jar.jar
-outjars     bin/classes-out.jar
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

-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
