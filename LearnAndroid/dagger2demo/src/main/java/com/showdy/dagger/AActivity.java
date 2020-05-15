package com.showdy.dagger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medisana.dagger2demo.R;
import com.showdy.dagger.di.AModule;
import com.showdy.dagger.di.DaggerAComponent;
import com.showdy.dagger.obj.PhotoTailor;

import javax.inject.Inject;

public class AActivity extends AppCompatActivity {

    private static final String TAG = "PhotoAActivity";

    @Inject
    PhotoTailor mTailor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dagger2);

        DaggerAComponent.builder()
                .baseComponent(((PhotoApplication) getApplication()).getComponent())
                .aModule(new AModule())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate: "+mTailor.hashCode());
    }


    public void jumpSecondActivity(View view) {
        startActivity(new Intent(this, BActivity.class));
    }
}
