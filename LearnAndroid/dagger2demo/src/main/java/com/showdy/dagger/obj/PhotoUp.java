package com.showdy.dagger.obj;

import android.util.Log;

import javax.inject.Inject;

/**
 * 演示Inject修饰构造方法
 */
public class PhotoUp {

    private static final String TAG = "PhotoUp";

    @Inject
    public PhotoUp() {
    }

    public void uploadPhoto(){
        Log.d(TAG, "uploadPhoto: ");
    }
}
