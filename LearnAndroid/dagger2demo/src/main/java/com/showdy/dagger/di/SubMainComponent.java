package com.showdy.dagger.di;

import com.showdy.dagger.MainActivity;

import dagger.Subcomponent;

@PhotoScope
@Subcomponent(modules = PhotoModule.class)
public interface SubMainComponent {

    void inject(MainActivity activity);
}
