package com.showdy.component.di;

import com.showdy.component.LocationActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class,modules = ActivityModule.class)
@ActivityScope
public interface ActivityComponent {

    void inject(LocationActivity activity);


    FragmentComponent.Builder provideFragmentComponent();
}
