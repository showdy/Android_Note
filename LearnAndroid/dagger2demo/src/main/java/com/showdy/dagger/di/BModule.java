package com.showdy.dagger.di;

import com.showdy.dagger.obj.Photo;
import com.showdy.dagger.obj.PhotoTailor;

import dagger.Module;
import dagger.Provides;

@Module
public class BModule {

    @PhotoScope
    @Provides
    public Photo providePhoto(){
        return new Photo("figure");
    }

//    @PhotoScope
//    @Provides
//    public PhotoTailor providePhotoTailor(){
//        return new PhotoTailor();
//    }
}
