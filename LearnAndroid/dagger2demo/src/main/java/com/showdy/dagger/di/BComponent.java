package com.showdy.dagger.di;

import com.showdy.dagger.BActivity;
import com.showdy.dagger.MainActivity;

import dagger.Component;

@Component(dependencies = {BaseComponent.class}, modules = {BModule.class})
@PhotoScope
public interface BComponent {

    void inject(BActivity activity);
}
