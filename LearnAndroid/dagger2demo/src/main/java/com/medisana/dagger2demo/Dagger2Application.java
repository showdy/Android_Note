package com.medisana.dagger2demo;

import android.app.Application;

import com.medisana.dagger2demo.di.DaggerMyComponent;
import com.medisana.dagger2demo.di.DaggerPresentComponent;
import com.medisana.dagger2demo.di.DatabaseModule;
import com.medisana.dagger2demo.di.HttpModel;
import com.medisana.dagger2demo.di.MyComponent;

public class Dagger2Application extends Application {


    private MyComponent mMyComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        //提升component生命周期为全局，实现全局单例
        mMyComponent = DaggerMyComponent.builder()
                .httpModel(new HttpModel())
                .databaseModule(new DatabaseModule())
                .presentComponent(DaggerPresentComponent.create())
                .build();
    }


    public MyComponent getMyComponent() {
        return mMyComponent;
    }
}
