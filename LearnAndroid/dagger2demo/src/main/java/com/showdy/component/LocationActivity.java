package com.showdy.component;

import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medisana.dagger2demo.R;
import com.showdy.component.di.ActivityComponent;
import com.showdy.component.di.ActivityModule;
import com.showdy.component.di.DaggerActivityComponent;
import com.showdy.component.di.FragmentComponent;

import javax.inject.Inject;


public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";

    @Inject
    LocationManager mLocationManager;

    FragmentComponent fragmentComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);


        ActivityComponent activityComponent = DaggerActivityComponent.builder()
                .appComponent(((ComponentApplicaiton) getApplication()).getAppComponent())
                .activityModule(new ActivityModule())
                .build();
        fragmentComponent = activityComponent.provideFragmentComponent()
                .build();
        activityComponent.inject(this);

        Log.d(TAG, "onCreate: " + mLocationManager.hashCode());


        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new LocationFragment(), LocationFragment.class.getSimpleName())
                .commit();
    }
}
