package com.showdy.learnandroid.android9;

import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by <b>Showdy</b> on 2020/9/11 15:15
 * 将 Class 的 classloader 置空,从而系统自动调用了 BootCalssLoader 去加载,即成功伪造成了系统方法去反射灰名单字段,绕过了检查机制,思路也是很棒
 */
class AndroidPReflection {

    static {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                Class classClazz = Class.class;
                // light greyList
                Field classLoaderField = classClazz.getDeclaredField("classLoader");
                classLoaderField.setAccessible(true);
                classLoaderField.set(AndroidPReflection.class, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static Field getDeclaredField(Class<?> clz, String name) throws NoSuchFieldException {
        return clz.getDeclaredField(name);
    }

    public static Method getDeclaredMethod(Class<?> clz, String name, Class<?>... parameterType)
            throws NoSuchMethodException {
        return clz.getDeclaredMethod(name, parameterType);
    }

    public static <T> Constructor<T> getDeclaredConstructor(Class<T> clz, Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        return clz.getConstructor(parameterTypes);
    }


}
