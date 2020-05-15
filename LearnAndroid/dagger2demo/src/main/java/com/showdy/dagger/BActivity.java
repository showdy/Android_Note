package com.showdy.dagger;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medisana.dagger2demo.R;
import com.showdy.dagger.di.BModule;
import com.showdy.dagger.di.DaggerBComponent;
import com.showdy.dagger.di.PhotoComponent;
import com.showdy.dagger.obj.PhotoTailor;

import javax.inject.Inject;

public class BActivity extends AppCompatActivity {

    private static final String TAG = "PhotoBActivity";

    @Inject
    PhotoTailor mTailor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dagger2);


        DaggerBComponent.builder()
                .baseComponent(((PhotoApplication)getApplication()).getComponent())
                .bModule(new BModule())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate: "+mTailor.hashCode());

    }


}
