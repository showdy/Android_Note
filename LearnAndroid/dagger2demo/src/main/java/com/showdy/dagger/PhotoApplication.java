package com.showdy.dagger;

import android.app.Application;

import com.showdy.dagger.di.BaseComponent;
import com.showdy.dagger.di.BaseModule;
import com.showdy.dagger.di.DaggerBaseComponent;

public class PhotoApplication extends Application {

    BaseComponent component;


    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerBaseComponent.builder().baseModule(new BaseModule()).build();
    }

    public BaseComponent getComponent() {
        return component;
    }
}
