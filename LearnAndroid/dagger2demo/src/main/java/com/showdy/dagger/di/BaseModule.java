package com.showdy.dagger.di;

import com.showdy.dagger.obj.PhotoTailor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BaseModule {

    @Singleton
    @Provides
    public PhotoTailor providePhotoTailor() {
        return new PhotoTailor("figure");
    }

}
