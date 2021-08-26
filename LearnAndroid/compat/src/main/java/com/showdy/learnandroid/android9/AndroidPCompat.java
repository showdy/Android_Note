package com.showdy.learnandroid.android9;

import android.annotation.SuppressLint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by <b>Showdy</b> on 2020/9/11 15:15
 *
 * 如下是一种解决方案：来自 https://github.com/whataa/pandora/blob/master/pandora-core/src/main/java/tech/linjiang/pandora/util/Reflect28Util.java
 *
 * 另一种： http://weishu.me/2019/03/16/another-free-reflection-above-android-p/
 */
public class AndroidPCompat {

    /**
     * 去掉在Android 上的提醒弹窗 （Detected problems with API compatibility(visit g.co/dev/appcompat for more info)
     * Android O 27 后谷歌限制了开发者调用非官方公开API 方法或接口，也就是说，你用反射直接调用源码就会有这样的提示弹窗出现，
     * 非 SDK 接口指的是 Android 系统内部使用、并未提供在 SDK 中的接口，开发者可能通过 Java 反射、JNI 等技术来调用这些接口
     *
     * Android P:当在 targetSdkVersion 为28时,系统是不允许反射灰名单中的 API
     *
     * 在application的attachBaseContext中调用即可
     */
    @SuppressLint("PrivateApi")
    public static void closeAndroidPDialog() {
        try {
            Class<?> aClass = AndroidPReflection.forName("android.content.pm.PackageParser$Package");
            Constructor<?> declaredConstructor = AndroidPReflection.getDeclaredConstructor(aClass,String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = AndroidPReflection.forName("android.app.ActivityThread");
            Method declaredMethod = AndroidPReflection.getDeclaredMethod(cls,"currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = AndroidPReflection.getDeclaredField(cls,"mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
