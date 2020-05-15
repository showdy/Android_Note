package com.showdy.component.di;

import android.location.LocationManager;
import android.location.LocationProvider;


import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    @Provides
    @FragmentScope
    public LocationProvider provideLocationProvider(LocationManager manager){
        return manager.getProvider("gps");
    }
}
