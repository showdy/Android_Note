package com.showdy.component;

import android.app.Application;

import com.showdy.component.di.AppComponent;
import com.showdy.component.di.ApplicationModule;
import com.showdy.component.di.DaggerAppComponent;

public class ComponentApplicaiton extends Application {
    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }


    public AppComponent getAppComponent() {
        return component;
    }
}
