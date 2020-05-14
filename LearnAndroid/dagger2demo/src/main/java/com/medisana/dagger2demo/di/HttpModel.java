package com.medisana.dagger2demo.di;

import com.medisana.dagger2demo.di.scope.AppScope;
import com.medisana.dagger2demo.obj.HttpObject;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 提供对象
 *
 * 方法中有一个单例--》 对应的component也需要单例
 */
@Module
public class HttpModel {
    @Provides
//    @Singleton
    @AppScope
    public HttpObject provideHttpObject(){
        return new HttpObject();
    }


//    @Provides
//    @AppScope
//    @Named("--")
//    public HttpObject provideHttpObject(String version){
//        return new HttpObject(version);
//    }

}
