package com.showdy.dagger.di;

import com.showdy.dagger.AActivity;
import com.showdy.dagger.MainActivity;

import dagger.Component;
import dagger.Module;

//Singleton 的组件不能依赖其他 scope 的组件，只能其他 scope 的组件可以依赖 Singleton的组件 。
@Component(dependencies = {BaseComponent.class}, modules = {AModule.class})
@PhotoScope
public interface AComponent {

    void inject(AActivity activity);
}
