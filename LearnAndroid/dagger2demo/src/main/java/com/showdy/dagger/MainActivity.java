package com.showdy.dagger;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medisana.dagger2demo.R;
import com.showdy.dagger.di.CustomQualier;
import com.showdy.dagger.di.DaggerPhotoComponent;
import com.showdy.dagger.di.PhotoModule;
import com.showdy.dagger.obj.Photo;
import com.showdy.dagger.obj.PhotoMananger;
import com.showdy.dagger.obj.PhotoTailor;
import com.showdy.dagger.obj.PhotoUp;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import dagger.Lazy;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";

    @Inject
    PhotoMananger mMananger;

    @Inject
    PhotoTailor mTailor;

    @Inject
    PhotoUp mPhotoUp;

//    //Lazy用于延迟加载,所谓的懒加载就是当你需要用到该依赖对象时,Dagger2才帮你去获取一个;
//    @Inject
//    @Named("figure")
//    Lazy<Photo> figurePhoto;
//
//    //Provide用于强制重新加载,也就是每一要用到依赖对象时,Dagger2都会帮你依赖注入一次。
//    @Inject
//    @Named("scenery")
//    Provider<Photo> sceneryPhoto;

    @Inject
    @Named("figure")
    Photo mFigurePhoto;

    @Inject
    @Named("scenery")
    Photo mSceneryPhoto;


    @Inject
    @CustomQualier("figure")
    Photo mCustomQualifierPhoto;


    String photoUrl = "android/xxx.com/112.jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerPhotoComponent.builder()
                //传参
                .photoModule(new PhotoModule(this))
                .build()
                .injectMainActivity(this);

        mMananger.startMethod();

        //上传
        mPhotoUp.uploadPhoto();

        Log.d(TAG, "onCreate: " + mFigurePhoto.hashCode());
        Log.d(TAG, "onCreate: " + mSceneryPhoto.hashCode());
        Log.d(TAG, "onCreate: " + mCustomQualifierPhoto.hashCode());

        Log.d(TAG, "onCreate:          photoTailor== " + mTailor.hashCode());
        Log.d(TAG, "onCreate: photoMananger tailor== " + mMananger.getTailor().hashCode());
    }
}
