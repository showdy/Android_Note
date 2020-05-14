package com.medisana.dagger2demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medisana.dagger2demo.di.DaggerMyComponent;
import com.medisana.dagger2demo.di.DatabaseModule;
import com.medisana.dagger2demo.di.HttpModel;
import com.medisana.dagger2demo.obj.DatabaseObject;
import com.medisana.dagger2demo.obj.HttpObject;
import com.medisana.dagger2demo.obj.MyPreseneter;

import javax.inject.Inject;

public class DaggerDemoActivity extends AppCompatActivity {

    private static final String TAG = "DaggerDemoActivity";

    //Dagger2 singleton实现是局部单例

    @Inject
    HttpObject mHttpObject;

    @Inject
    HttpObject mHttpObject2;


    @Inject
    DatabaseObject mDatabaseObject;

    @Inject
    MyPreseneter mMyPreseneter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dagger2);

//        DaggerMyComponent.create().inject(this);

//        DaggerMyComponent.builder()
//                .httpModel(new HttpModel())
//                .databaseModule(new DatabaseModule())
//                .build()
//                .inject(this);

        ((Dagger2Application) getApplication()).getMyComponent().inject(this);

        Log.d(TAG, "onCreate: httpobject =" + mHttpObject.hashCode());
        Log.d(TAG, "onCreate: httpobject2=" + mHttpObject2.hashCode());
        Log.d(TAG, "onCreate: databaseObject" + mDatabaseObject.hashCode());
        Log.d(TAG, "onCreate: mypresenter==" + mMyPreseneter.hashCode());
    }

    public void jumpSecondActivity(View view) {

        startActivity(new Intent(this, DaggerSecondActivity.class));
    }
}
