package com.medisana.dagger2demo.di;

import com.medisana.dagger2demo.DaggerDemoActivity;
import com.medisana.dagger2demo.DaggerSecondActivity;
import com.medisana.dagger2demo.di.scope.AppScope;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 一个组件，用来注入
 * <p>
 * scope是用来标注组件的，方便进行依赖关系的绑定
 * dependencies使用：
 * 多个组件之间的scope不能相同，没有scope的不能依赖有scope的组件
 */
//@Singleton
@AppScope
@Component(dependencies = {PresentComponent.class}, modules = {HttpModel.class, DatabaseModule.class})
public interface MyComponent {
    /**
     * 参数无法使用多态
     *
     * @param activity
     */
    void inject(DaggerDemoActivity activity);


    void inject(DaggerSecondActivity activity);
}
