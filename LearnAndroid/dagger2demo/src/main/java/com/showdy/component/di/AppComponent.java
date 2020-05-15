package com.showdy.component.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ApplicationModule.class)
@Singleton
public interface AppComponent {

   public Context provideContext();
}
