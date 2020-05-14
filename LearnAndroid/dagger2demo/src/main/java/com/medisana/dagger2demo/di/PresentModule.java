package com.medisana.dagger2demo.di;

import com.medisana.dagger2demo.di.scope.UserScope;
import com.medisana.dagger2demo.obj.MyPreseneter;

import dagger.Module;
import dagger.Provides;

/**
 * @userScope功能和singleton类似
 */
@Module
public class PresentModule {
    @UserScope
    @Provides
    public MyPreseneter providePrensenter(){
        return  new MyPreseneter();
    }
}
