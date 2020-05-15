package com.showdy.dagger.di;

import com.showdy.dagger.obj.PhotoTailor;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;

@Component(modules = {BaseModule.class})
@Singleton
public interface BaseComponent {

    public PhotoTailor providePhotoTailor();


//    SubMainComponent prvoideSubMainComponent(PhotoModule module);
}
