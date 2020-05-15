package com.showdy.component.di;

import android.content.Context;
import android.location.LocationManager;

import com.medisana.dagger2demo.di.scope.AppScope;

import dagger.Module;
import dagger.Provides;

//表明继承关系
@Module(subcomponents = FragmentComponent.class)
public class ActivityModule {

    @Provides
    @ActivityScope
    LocationManager provideLocalManager(Context context){
        return (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
}
