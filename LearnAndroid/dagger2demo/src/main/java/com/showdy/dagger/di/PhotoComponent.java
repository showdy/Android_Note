package com.showdy.dagger.di;

import com.showdy.dagger.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {PhotoModule.class})
//@Singleton
@PhotoScope
public interface PhotoComponent {

    void injectMainActivity(MainActivity activity);
}
