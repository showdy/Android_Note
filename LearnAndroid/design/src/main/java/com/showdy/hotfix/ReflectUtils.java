package com.showdy.hotfix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {

    public static Field findField(Object object, String name) throws NoSuchFieldException {
        Class<?> clazz = object.getClass();
        try {
            while (clazz != Object.class) {
                Field field = clazz.getDeclaredField(name);
                if (field != null) {
                    //允许访问
                    field.setAccessible(true);
                    return field;
                }
                //可能属性在父类中...
                clazz = clazz.getSuperclass();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new NoSuchFieldException(object.getClass().getSimpleName() + "not find field" + name);
    }

    public static Method findMethod(Object object, String name,Class<?>... paramsTypes) throws NoSuchMethodException {
        Class<?> clazz = object.getClass();
        try {
            while (clazz != Object.class) {
                Method method = clazz.getDeclaredMethod(name, paramsTypes);
                if (method != null) {
                    //允许访问
                    method.setAccessible(true);
                    return method;
                }
                //可能属性在父类中...
                clazz = clazz.getSuperclass();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new NoSuchMethodException(object.getClass().getSimpleName() + "not find method " + name);
    }

}
