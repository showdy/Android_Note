package com.showdy.dagger.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Scope的作用仅仅只是保证实例是否复用，与生命周期无关。
 *
 * module 的 provide 方法使用了 scope ，那么 component 就必须使用同一个注解
 *
 * @Singleton的生命周期依附于component，同一个module被多个component获取依赖对象将不是同一个，
 * @Singleton的生命周期属于Activity级别（不严谨），定义全局单例，需要在application中初始化component
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface PhotoScope {}
