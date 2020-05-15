package com.showdy.dagger.di;

import android.content.Context;

import com.showdy.dagger.obj.Photo;
import com.showdy.dagger.obj.PhotoMananger;
import com.showdy.dagger.obj.PhotoTailor;
import com.showdy.dagger.obj.PhotoTaker;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PhotoModule {

    Context context;

    String photoUrl;

    public PhotoModule(Context context, String photoUrl) {
        this.context = context;
        this.photoUrl = photoUrl;
    }


    public PhotoModule(Context context) {
        this.context = context;
    }

    @Provides
    public PhotoTaker providePhotoTaker() {
        return new PhotoTaker(context);
    }

    //单例--局部单例
    @Provides
//    @Singleton
    @PhotoScope
    public PhotoTailor providePhotoTailor() {
        return new PhotoTailor(photoUrl);
    }


    @Provides
    public String providePhotoUrl() {
        return "android/xxx.com/112.jpg";
    }


    @Provides
    public PhotoMananger providePhotoManager(PhotoTaker taker, PhotoTailor tailor) {
        return new PhotoMananger(taker, tailor);
    }


    //    @Provides
//    @Named("tailorPhoto")
//    public PhotoTailor providePhotoTailorByPhoto() {
//        Photo photo = new Photo("scenery");
//        return new PhotoTailor(photo);
//    }
    //一个依赖类以另外一个类作为依赖类
    //Dagger2会像帮依赖需求方找依赖对象一样帮你找到该方法依赖的Photo 实例
//    @Provides
//    @Named("tailorPhotoByPhoto")
//    public PhotoTailor providePhotoTailorByPhoto(Photo photo) {
//        return new PhotoTailor(photo);
//    }
//
//    @Provides
//    public Photo getPhoto() {
//        return new Photo("scenery");
//    }


    // Dagger2是通过返回值类型来确定依赖，两个方法都返回Photo,Dagger2无法区分
    //使用 Qualier注解区分

    @Named("figure")
    @Provides
    public Photo getFigurePhoto() {
        return new Photo("figure");
    }

    @Named("scenery")
    @Provides
    public Photo getSceneryPhoto() {
        return new Photo("Scenery");
    }

    @CustomQualier("figure")
    @Provides
    public Photo customQualifierPhoto() {
        return new Photo("figure");
    }
}
