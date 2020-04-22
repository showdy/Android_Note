package com.medisana.hotfix;

import android.app.Application;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HotFix {

    /**
     * 1. 获取当前应用的PathClassLoader
     * <p>
     * 2. 反射获取DexPathList对象属性pathList
     * <p>
     * 3. 反射修改pathList的dexElements数组
     * <p>
     * 1）把补丁包path.dex 转化位Element[]
     * 2) 获取pathList的dexElements
     * 3) patch+old合并，并反射赋值给dexElements
     */
    public static void installPatch(Application application, File patch) {
        if (!patch.exists()) {
            return;
        }
        //pathClassLoader
        ClassLoader classLoader = application.getClassLoader();

        //##########################################
        //android N 混编问题，导致无法类替换，就使用自定义的ClassLoader
        //修改LoadedApk，Resources，DrawableInflater中成员变量mClassLoader
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                classLoader = NewClassLoaderInjector.inject(application, classLoader);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        //##########################################

        try {
            Field pathListField = ReflectUtils.findField(classLoader, "pathList");
            Object pathList = pathListField.get(classLoader);

            //3.1
            ArrayList<Object> patchs = new ArrayList<>();
            patchs.add(patch);
            //私有目录 odex输出目录
            File file = application.getFilesDir();
            //记录异常
            ArrayList<IOException> exceptions = new ArrayList<>();
            //修改dexElements
            Method method = ReflectUtils.findMethod(pathList, "makePathElements", List.class, File.class, List.class);
            //静态方法
            Object[] patchElements = (Object[]) method.invoke(null, patchs, file, exceptions);

            //3.2
            Field dexElementsField = ReflectUtils.findField(pathList, "dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(pathList);

            //3.3
            Object[] newElements = (Object[]) Array.newInstance(patchElements.getClass().getComponentType(),
                    patchElements.length + dexElements.length);
            System.arraycopy(patchElements, 0, newElements, 0, patchElements.length);
            System.arraycopy(dexElements, 0, newElements, patchElements.length, dexElements.length);
            //修改为新值
            dexElementsField.set(pathList, newElements);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }
}
