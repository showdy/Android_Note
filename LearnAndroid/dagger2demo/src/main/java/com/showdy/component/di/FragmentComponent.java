package com.showdy.component.di;

import com.showdy.component.LocationFragment;

import dagger.Subcomponent;

@Subcomponent(modules = FragmentModule.class)
@FragmentScope
public interface FragmentComponent {
    void inject(LocationFragment fragment);

    //表明继承关系
    @Subcomponent.Builder
    interface Builder{
        FragmentComponent build();
    }
}
