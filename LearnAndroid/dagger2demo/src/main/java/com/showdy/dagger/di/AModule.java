package com.showdy.dagger.di;

import com.showdy.dagger.obj.Photo;
import com.showdy.dagger.obj.PhotoTailor;
import com.showdy.dagger.obj.PhotoUp;

import dagger.Module;
import dagger.Provides;

@Module
public class AModule {

    @PhotoScope
    @Provides
    public Photo providePhoto(){
        return new Photo("scenery");
    }

//    @PhotoScope
//    @Provides
//    public PhotoTailor providePhotoTailor(){
//        return new PhotoTailor();
//    }
}
