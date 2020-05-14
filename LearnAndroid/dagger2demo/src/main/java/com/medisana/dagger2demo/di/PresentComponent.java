package com.medisana.dagger2demo.di;


import com.medisana.dagger2demo.di.scope.UserScope;
import com.medisana.dagger2demo.obj.MyPreseneter;

import dagger.Component;
import dagger.Provides;

@UserScope
@Component(modules = PresentModule.class)
public interface PresentComponent {

    //声明和module一样的方法即可，不会直接在activity中注入使用
    MyPreseneter providePrensenter();
}
